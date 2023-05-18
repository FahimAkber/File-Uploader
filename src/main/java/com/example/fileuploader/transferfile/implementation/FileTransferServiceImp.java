package com.example.fileuploader.transferfile.implementation;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.FileThread;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.transferfile.FileTransferService;
import com.jcraft.jsch.*;
import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.model.Configuration;
import com.example.fileuploader.model.JobInfo;
import com.jcraft.jsch.jcraft.Compression;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
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


    public Session createSession(String remoteUser, String remoteHost, int remotePort) {
        Session session = null;
        try {
            jSch.addIdentity("D:/mfs-key.pem");
            session = jSch.getSession(remoteUser, remoteHost, remotePort);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("compression.s2c", "zlib,none");
            session.setConfig("compression.c2s", "zlib,none");
            session.setConfig("rcvbuf", "1048576"); // 1 MB
            session.setConfig("sndbuf", "1048576");
            session.setConfig("sftp.max_packet", "131072"); // Set maximum packet size to 131072 bytes (128 KB)

            session.connect();
        } catch (JSchException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return session;
    }

    @Override
    public Session createSession(String remoteUser, String remotePassword, String remoteHost, int remotePort) {
        return null;
    }

    @Override
    public ChannelSftp createChannelSftp(Session session) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
        } catch (JSchException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return channelSftp;
    }
    @Override
    public void getFiles(JobInfo jobInfo){

    }

    @Override
    public void getTestFiles() {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            File localFile = new File("D:/Bin Files/");
            if(!localFile.exists()){
                localFile.mkdirs();
            }
            session = createSession("root", "103.36.101.99", 22);
            channelSftp = createChannelSftp(session);
            String concatRemotePath = "/files/";
            String concatLocalPath = localFile.getPath();
            channelSftp.cd(concatRemotePath);
            Vector list = channelSftp.ls("*.bin");

            Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(list, 500);
           destroyConnection(session, channelSftp);

            for (List<ChannelSftp.LsEntry> objects : partition) {
                FileThread fileThread = new FileThread(createSession("root", "103.36.101.99", 22), objects, concatLocalPath, concatRemotePath);
                Thread thread = new Thread(fileThread);
                thread.start();
//                for(ChannelSftp.LsEntry entry : objects){
//                    uploadFileToLocalDestination(entry, concatLocalPath, channelSftp);
//                }

            }
        } catch ( SftpException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploaderException exception){
            throw exception;
        } finally {
            destroyConnection(session, channelSftp);
        }
    }

    @Override
    public void setFiles(JobInfo jobInfo) {
//        if(!jobQueue.contains(jobInfo.getJobType())){
//            jobQueue.add(jobInfo.getJobType());
//            Session session = null;
//            ChannelSftp channelSftp = null;
//
//            try {
//                File localFile = createDirIfNotExist(jobInfo.getLocalPath(), jobInfo.getFileExtension());
//                session = createSession(jobInfo.getRemoteUser(), jobInfo.getRemotePassword(), jobInfo.getRemoteHost(), jobInfo.getRemotePort());
//                channelSftp = createChannelSftp(session);
//                File[] files = Objects.requireNonNull(localFile.listFiles());
//                Partition<File> partition = Partition.getPartitionInstance(Arrays.asList(files), 500);
//                for(int i = 0; i < partition.size(); i++){
//                    List<File> objects = partition.get(i);
//                    List<String> fileNames = objects.stream().map(File::getName).collect(Collectors.toList());
//                    List<String> checkedFiles = fileService.getCheckedFiles(fileNames);
//                    for(File file : objects){
//                        if(!checkedFiles.contains(file.getName())){
//                            channelSftp.put(file.getAbsolutePath(), jobInfo.getRemotePath().concat("/").concat(jobInfo.getRemoteExtension()));
//                            fileService.save(new UploadedFile(file.getName()));
//                            LoggerFactory.getLogger(LOGGER_NAME).info("Successfully uploaded outward file: {}", file.getName());
//                        }else{
//                            LoggerFactory.getLogger(LOGGER_NAME).info("Already uploaded outward file: {}", file.getName());
//                        }
//                    }
//                }
//            } catch ( SftpException e) {
//                throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            } catch (FileUploaderException exception){
//                throw exception;
//            } finally {
//                jobQueue.remove(jobInfo.getJobType());
//                destroyConnection(session, channelSftp);
//                LoggerFactory.getLogger("End Now: " + Calendar.getInstance().getTime());
//            }
//        }else{
//            LoggerFactory.getLogger(LOGGER_NAME).info("Previous Job: {} is running.", jobInfo.getJobType());
//        }
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
        LocalDateTime startTime = LocalDateTime.now();
        channelSftp.get(entry.getFilename(), localPath);
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        LoggerFactory.getLogger(LOGGER_NAME).info("Successfully imported inward File: {}, start at: {}, end at: {}, total time: {}", entry.getFilename(), startTime.toString(), endTime.toString(), duration.getSeconds());
    }
    private File createDirIfNotExist(String rootPath, String extension){
        File file = new File(rootPath.concat("/").concat(extension).concat("/").concat(getCurrentDate()));
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
