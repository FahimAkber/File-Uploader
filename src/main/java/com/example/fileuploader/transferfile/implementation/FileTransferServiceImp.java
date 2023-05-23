package com.example.fileuploader.transferfile.implementation;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.Status;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.transferfile.FileTransferService;
import com.jcraft.jsch.*;
import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.model.Configuration;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileTransferServiceImp implements FileTransferService {

    private final UploadedFileService fileService;
    private final QuartzJobInfoService quartzJobInfoService;
    private Configuration configuration;
    private JSch jSch;
    private Queue<String> jobQueue;
    private static final String LOGGER_NAME = "File Uploader";

    public FileTransferServiceImp(UploadedFileService fileService, QuartzJobInfoService quartzJobInfoService, Configuration configuration) {
        this.fileService = fileService;
        this.quartzJobInfoService = quartzJobInfoService;
        this.configuration = configuration;
        jSch = new JSch();
        jobQueue = new LinkedList<>();
    }


    @Override
    public Session createSession(String remoteUser, String remoteHost, int remotePort, String fileName) {
        Session session = null;
        try {
            jSch.addIdentity(fileName);
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
    public void getFiles(QuartzJobInfo jobInfo){
        if(!jobQueue.contains(jobInfo.getJobKey())){
            jobQueue.add(jobInfo.getJobKey());
            Session session = null;
            ChannelSftp channelSftp = null;

            try {
                File localFile = createDirIfNotExist(configuration.getLocalFileLocation());
                session = createSession(jobInfo.getSourceUser(), jobInfo.getSourceHost(), jobInfo.getSourcePort(), jobInfo.getSourceFileName());
                channelSftp = createChannelSftp(session);
                String concatLocalPath = localFile.getPath();
                channelSftp.cd(jobInfo.getSourcePath());
                Vector list = channelSftp.ls(jobInfo.getSourcePath().concat("/*").concat(jobInfo.getFileExtension()));

                Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(list, 500);
                for (List<ChannelSftp.LsEntry> objects : partition) {
                    List<String> fileNames = objects.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());
                    List<String> checkedFiles = fileService.getCheckedFiles(fileNames);

                    for(ChannelSftp.LsEntry entry : objects){
                        if(!checkedFiles.contains(entry.getFilename())){
                            uploadFileToLocalDestination(entry.getFilename(), concatLocalPath, channelSftp, jobInfo.getJobKey());
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
                jobQueue.remove(jobInfo.getJobKey());
                destroyConnection(session, channelSftp);
            }
        }else{
            LoggerFactory.getLogger(LOGGER_NAME).info("Previous job: {} is running.", jobInfo.getJobKey());
        }
    }

    @Override
    public void getTestFiles() {
//        Session session = null;
//        ChannelSftp channelSftp = null;
//
//        try {
//            File localFile = new File("D:/Bin Files/");
//            if(!localFile.exists()){
//                localFile.mkdirs();
//            }


//            session = createSession("root", "103.36.101.99", 22, "D:/pem-key");
//            channelSftp = createChannelSftp(session);
//            String concatRemotePath = "/files/";
//            String concatLocalPath = localFile.getPath();
//            channelSftp.cd(concatRemotePath);
//            Vector list = channelSftp.ls("*.bin");
//
//            Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(list, 500);
//           destroyConnection(session, channelSftp);
//
//            for (List<ChannelSftp.LsEntry> objects : partition) {
//                FileThread fileThread = new FileThread(createSession("root", "103.36.101.99", 22), objects, concatLocalPath, concatRemotePath);
//                Thread thread = new Thread(fileThread);
//                thread.start();
////                for(ChannelSftp.LsEntry entry : objects){
////                    uploadFileToLocalDestination(entry, concatLocalPath, channelSftp);
////                }
//
//            }
//        } catch ( SftpException | NullPointerException e) {
//            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (FileUploaderException exception){
//            throw exception;
//        } finally {
//            destroyConnection(session, channelSftp);
//        }
    }

    @Override
    public void setFiles() {
        Map<String, String[]> keyWiseFile = fileService.getKeyWiseFileByStatus(Status.RECEIVED.value);
        for(String jobKey : keyWiseFile.keySet()){
            String containerKey = "Sender Job : ".concat(jobKey);
            QuartzJobInfo jobInfo = quartzJobInfoService.findJobInfoByJobKey(jobKey);
            if(!jobQueue.contains(containerKey)){
                jobQueue.add(containerKey);
                Session session = null;
                ChannelSftp channelSftp = null;

                try {
                    File localFile = createDirIfNotExist(configuration.getLocalFileLocation());
                    session = createSession(jobInfo.getDestinationUser(), jobInfo.getDestinationHost(), jobInfo.getDestinationPort(), jobInfo.getDestinationFileName());
                    channelSftp = createChannelSftp(session);
                    String[] fileNames = keyWiseFile.get(jobKey);
                    for(String fileName : fileNames){
                        File file = new File(localFile, fileName);
                        if(file.exists()){
                            channelSftp.put(file.getAbsolutePath(), jobInfo.getDestinationPath());
                            fileService.updateStatusOfFile(fileName, Status.SENT.value);
                            LoggerFactory.getLogger(LOGGER_NAME).info("Successfully uploaded outward file: {}", file.getName());
                        }
                    }
                } catch ( SftpException e) {
                    throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                catch (FileUploaderException exception){
                    throw exception;
                } finally {
                    jobQueue.remove(containerKey);
                    destroyConnection(session, channelSftp);
                    LoggerFactory.getLogger("End Now: " + Calendar.getInstance().getTime());
                }
            }else{
                LoggerFactory.getLogger(LOGGER_NAME).info("Previous Job: {} is running.", containerKey);
            }
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
    private void uploadFileToLocalDestination(String fileName, String localPath, ChannelSftp channelSftp, String jobKey) throws SftpException {
        LocalDateTime startTime = LocalDateTime.now();
        channelSftp.get(fileName, localPath);
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        fileService.save(new UploadedFile(fileName, jobKey, Status.RECEIVED.value, endTime.toLocalDate()));
        LoggerFactory.getLogger(LOGGER_NAME).info("Successfully imported inward File: {}, start at: {}, end at: {}, total time: {}", fileName, startTime.toString(), endTime.toString(), duration.getSeconds());
    }
    private File createDirIfNotExist(String rootPath){
        File file = new File(rootPath);
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
