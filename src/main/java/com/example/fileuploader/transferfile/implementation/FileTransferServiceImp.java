package com.example.fileuploader.transferfile.implementation;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.transferfile.FileTransferService;
import com.jcraft.jsch.*;
import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.model.Configuration;
import com.example.fileuploader.model.JobInfo;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileTransferServiceImp implements FileTransferService {

    private final UploadedFileService fileService;
    private Configuration configuration;
    private JSch jSch;
    private Queue<String> jobQueue;
    private static final String LOGGER_NAME = "File Uploader";

    public FileTransferServiceImp(UploadedFileService fileService, Configuration configuration) {
        this.fileService = fileService;
        this.configuration = configuration;
        jSch = new JSch();
        jobQueue = new LinkedList<>();
    }

    @Override
    public Session createSession(String remoteUser, String remotePassword, String remoteHost, int remotePort) {
        Session session = null;
        try {
            session = jSch.getSession(remoteUser, remoteHost, remotePort);
            session.setConfig(configuration.getConfigurationKey(), configuration.getConfigurationValue());
            session.setPassword(remotePassword);
            session.connect();
        } catch (JSchException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return session;
    }

    @Override
    public ChannelSftp createChannelSftp(Session session) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) session.openChannel(configuration.getChannelType());
            channelSftp.connect();
        } catch (JSchException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return channelSftp;
    }

    @Override
    public void getFiles(JobInfo jobInfo) {
        if(!jobQueue.contains(jobInfo.getJobType())){
            jobQueue.add(jobInfo.getJobType());
            Session session = null;
            ChannelSftp channelSftp = null;

            try {
                File localFile = createDirIfNotExist(jobInfo.getLocalPath(), jobInfo.getProjectName(), jobInfo.getLocalExtension());
                session = createSession(jobInfo.getRemoteUser(), jobInfo.getRemotePassword(), jobInfo.getRemoteHost(), jobInfo.getRemotePort());
                channelSftp = createChannelSftp(session);
                String concatRemotePath = jobInfo.getRemotePath().concat("/").concat(jobInfo.getProjectName()).concat("/").concat(jobInfo.getRemoteExtension()).concat("/");
                String concatLocalPath = localFile.getPath();
                channelSftp.cd(concatRemotePath);
                Vector list = channelSftp.ls(concatRemotePath);

                Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(list, 500);
                for (List<ChannelSftp.LsEntry> objects : partition) {
                    List<String> fileNames = objects.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());
                    List<String> checkedFiles = fileService.getCheckedFiles(fileNames);

                    for(ChannelSftp.LsEntry entry : objects){
                        if(!checkedFiles.contains(entry.getFilename())){
                            uploadFileToLocalDestination(entry, concatLocalPath, channelSftp);
                        }else{
                            LoggerFactory.getLogger(LOGGER_NAME).info("Already imported Inward File: {}", entry.getFilename());
                        }
                    }
                }
            } catch ( SftpException | NullPointerException e) {
                throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (FileUploaderException exception){
                throw exception;
            } finally {
                jobQueue.remove(jobInfo.getJobType());
                destroyConnection(session, channelSftp);
            }
        }else{
            LoggerFactory.getLogger(LOGGER_NAME).info("Previous job: {} is running.", jobInfo.getJobType());
        }
    }

    @Override
    public void setFiles(JobInfo jobInfo) {
        if(!jobQueue.contains(jobInfo.getJobType())){
            jobQueue.add(jobInfo.getJobType());
            Session session = null;
            ChannelSftp channelSftp = null;

            try {
                File localFile = createDirIfNotExist(jobInfo.getLocalPath(), jobInfo.getProjectName(), jobInfo.getLocalExtension());
                session = createSession(jobInfo.getRemoteUser(), jobInfo.getRemotePassword(), jobInfo.getRemoteHost(), jobInfo.getRemotePort());
                channelSftp = createChannelSftp(session);
                File[] files = Objects.requireNonNull(localFile.listFiles());
                Partition<File> partition = Partition.getPartitionInstance(Arrays.asList(files), 500);
                for(int i = 0; i < partition.size(); i++){
                    List<File> objects = partition.get(i);
                    List<String> fileNames = objects.stream().map(File::getName).collect(Collectors.toList());
                    List<String> checkedFiles = fileService.getCheckedFiles(fileNames);
                    for(File file : objects){
                        if(!checkedFiles.contains(file.getName())){
                            channelSftp.put(file.getAbsolutePath(), jobInfo.getRemotePath().concat("/").concat(jobInfo.getProjectName()).concat("/").concat(jobInfo.getRemoteExtension()));
                            fileService.save(new UploadedFile(file.getName()));
                            LoggerFactory.getLogger(LOGGER_NAME).info("Successfully uploaded outward file: {}", file.getName());
                        }else{
                            LoggerFactory.getLogger(LOGGER_NAME).info("Already uploaded outward file: {}", file.getName());
                        }
                    }
                }
            } catch ( SftpException e) {
                throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (FileUploaderException exception){
                throw exception;
            } finally {
                jobQueue.remove(jobInfo.getJobType());
                destroyConnection(session, channelSftp);
                LoggerFactory.getLogger("End Now: " + Calendar.getInstance().getTime());
            }
        }else{
            LoggerFactory.getLogger(LOGGER_NAME).info("Previous Job: {} is running.", jobInfo.getJobType());
        }
    }

    @Override
    public void destroyConnection(Session session, ChannelSftp channelSftp) {
        try{
            if(channelSftp != null){
                channelSftp.disconnect();
            }
            if(session != null){
                session.disconnect();
            }
        }catch (Exception e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private void uploadFileToLocalDestination(ChannelSftp.LsEntry entry, String localPath, ChannelSftp channelSftp) throws SftpException {
        channelSftp.get(entry.getFilename(), localPath);
        LoggerFactory.getLogger(LOGGER_NAME).info("Successfully imported inward File: {}", entry.getFilename());
        fileService.save(new UploadedFile(entry.getFilename()));
    }
    private File createDirIfNotExist(String rootPath, String projectName, String extension){
        File file = new File(rootPath.concat("/").concat(projectName).concat("/").concat(extension).concat("/").concat(getCurrentDate()));
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }
    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MMM/dd");
        return sdf.format(Calendar.getInstance().getTime());
    }

}
