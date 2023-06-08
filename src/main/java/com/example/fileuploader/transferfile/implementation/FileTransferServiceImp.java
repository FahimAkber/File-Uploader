package com.example.fileuploader.transferfile.implementation;

import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.Status;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.threadConfigurer.FileThread;
import com.example.fileuploader.threadConfigurer.PoolInstance;
import com.example.fileuploader.transferfile.FileTransferService;
import com.example.fileuploader.util.Util;
import com.jcraft.jsch.*;
import com.example.fileuploader.model.Configuration;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileTransferServiceImp implements FileTransferService {

    private final UploadedFileService fileService;
    private Configuration configuration;
    private Queue<String> jobQueue;
    private static final String LOGGER_NAME = "File Uploader";

    public FileTransferServiceImp(UploadedFileService fileService, Configuration configuration) {
        this.fileService = fileService;
        this.configuration = configuration;
        jobQueue = new LinkedList<>();
    }
    @Override
    public Session createSession(String remoteUser, String remoteHost, int remotePort, String fileName, String password) {
        Session session = null;
        JSch jSch = new JSch();
        try {
            if(fileName != null && !fileName.trim().isEmpty()){
                jSch.addIdentity(fileName);
                session = jSch.getSession(remoteUser, remoteHost, remotePort);
            }else{
                session = jSch.getSession(remoteUser, remoteHost, remotePort);
                session.setPassword(password);
            }
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
        } catch(Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return session;
    }
    @Override
    public ChannelSftp createChannelSftp(Session session) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.setBulkRequests(30);
            channelSftp.connect();
        } catch (JSchException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return channelSftp;
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
    @Override
    public void getFiles(QuartzJobInfo jobInfo){
        Session session = null;
        ChannelSftp channelSftp = null;
        Server sourceServer = jobInfo.getSourceServer(), destinationServer = jobInfo.getDestinationServer();
        String sourcePath = jobInfo.getSourcePath(), localBasePath = configuration.getLocalFileLocation().concat("/").concat(sourceServer.getHost()).concat("/").concat(jobInfo.getSourcePath()).concat("/");

        try {
            session = createSession(sourceServer.getUser(), sourceServer.getHost(), sourceServer.getPort(), sourceServer.getSecureFileName(), sourceServer.getPassword());
            channelSftp = createChannelSftp(session);

            Vector<ChannelSftp.LsEntry> childFolders = channelSftp.ls(sourcePath+"/child_*");
            LocalDateTime startTime = LocalDateTime.now();

            Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(childFolders, 5000);
            List<FileThread> tasks = new ArrayList<>();
            for(List<ChannelSftp.LsEntry> chunkFolder : partition){
                tasks.add(new FileThread(channelSftp, chunkFolder, jobInfo.getJobKey(), sourceServer.getHost(), sourcePath, localBasePath, destinationServer.getHost(), jobInfo.getDestinationPath(), jobInfo.getFileExtension(), fileService));
            }

            PoolInstance poolInstance = Util.poolInstance;
            poolInstance.setTasks(tasks);
            poolInstance.implementSingleInstance();

            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(startTime, endTime);
            LoggerFactory.getLogger(LOGGER_NAME).info("job started at: {}, end at: {}, completed at: {}", startTime.toString(), endTime.toString(), duration.getSeconds());
        } catch (FileUploaderException exception){
            throw exception;
        } catch (Exception e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            jobQueue.remove(jobInfo.getJobKey());
            destroyConnection(session, channelSftp);
        }
    }
     @Override
    public void setFiles() {
//        List<UploadedFileInfo> filesByStatusAndCriteria = fileService.getFilesByStatusAndCriteria(Status.RECEIVED.value);
//        for(UploadedFileInfo uploadedFileInfo : filesByStatusAndCriteria){
//            Server destinationServer = serverService.findByHost(uploadedFileInfo.getDestinationHost());
//            String destinationPath = uploadedFileInfo.getDestinationPath();
//            List<String> fileNames = uploadedFileInfo.getFileNames();
//
//            Session session = null;
//            ChannelSftp channelSftp = null;
//
//            try {
//                File localFile = createDirIfNotExist(configuration.getLocalFileLocation());
//                session = createSession(destinationServer.getUser(), destinationServer.getHost(), destinationServer.getPort(), destinationServer.getSecureFileName(), destinationServer.getPassword());
//                channelSftp = createChannelSftp(session);
//                for(String fileName : fileNames){
//                    File file = new File(localFile, fileName);
//                    if(file.exists()){
//                        channelSftp.put(file.getAbsolutePath(), destinationPath);
//                        fileService.updateStatusOfFile(fileName, Status.SENT.value);
//                        LoggerFactory.getLogger(LOGGER_NAME).info("Successfully uploaded outward file: {}", file.getName());
//                    }else{
//                        // File received but not found in local storage.
//                    }
//                }
//            } catch ( SftpException e) {
//                throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//            catch (FileUploaderException exception){
//                throw exception;
//            } finally {
//                destroyConnection(session, channelSftp);
//                LoggerFactory.getLogger("End Now: " + Calendar.getInstance().getTime());
//            }
//
//        }
//        Map<String, String[]> keyWiseFile = fileService.getKeyWiseFileByStatus(Status.RECEIVED.value);
//        for(String jobKey : keyWiseFile.keySet()){
//            String containerKey = "Sender Job : ".concat(jobKey);
//            QuartzJobInfo jobInfo = quartzJobInfoService.findJobInfoByJobKey(jobKey);
//            if(!jobQueue.contains(containerKey)){
//                jobQueue.add(containerKey);
//                Session session = null;
//                ChannelSftp channelSftp = null;
//
//                try {
//                    File localFile = createDirIfNotExist(configuration.getLocalFileLocation());
//                    session = createSession(jobInfo.getDestinationUser(), jobInfo.getDestinationHost(), jobInfo.getDestinationPort(), jobInfo.getDestinationFileName());
//                    channelSftp = createChannelSftp(session);
//                    String[] fileNames = keyWiseFile.get(jobKey);
//                    for(String fileName : fileNames){
//                        File file = new File(localFile, fileName);
//                        if(file.exists()){
//                            channelSftp.put(file.getAbsolutePath(), jobInfo.getDestinationPath());
//                            fileService.updateStatusOfFile(fileName, Status.SENT.value);
//                            LoggerFactory.getLogger(LOGGER_NAME).info("Successfully uploaded outward file: {}", file.getName());
//                        }
//                    }
//                } catch ( SftpException e) {
//                    throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//                catch (FileUploaderException exception){
//                    throw exception;
//                } finally {
//                    jobQueue.remove(containerKey);
//                    destroyConnection(session, channelSftp);
//                    LoggerFactory.getLogger("End Now: " + Calendar.getInstance().getTime());
//                }
//            }else{
//                LoggerFactory.getLogger(LOGGER_NAME).info("Previous Job: {} is running.", containerKey);
//            }
//        }
    }

}
