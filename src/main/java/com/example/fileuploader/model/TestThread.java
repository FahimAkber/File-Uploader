package com.example.fileuploader.model;

import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.service.UploadedFileService;
import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;

public class TestThread implements Runnable{


    private JSch jSch;
    private static final String LOGGER_NAME = "File Uploader";

    public TestThread() {
        jSch = new JSch();
    }
    @Override
    public void run() {
        getTestFiles();
    }
    public Session createSession(String remoteUser, String remoteHost, int remotePort) {
        Session session = null;
        try {
            jSch.addIdentity("D:/mfs-key.pem");
            session = jSch.getSession(remoteUser, remoteHost, remotePort);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("compression", "zlib");
            session.connect();
        } catch (JSchException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return session;
    }
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

            for (List<ChannelSftp.LsEntry> objects : partition) {
                for(ChannelSftp.LsEntry entry : objects){
                    uploadFileToLocalDestination(entry, concatLocalPath, channelSftp);
                }
            }
        } catch (SftpException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploaderException exception){
            throw exception;
        } finally {
            destroyConnection(session, channelSftp);
        }
    }
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
//        InputStream inputStream = channelSftp.get(entry.getFilename());
//        File file = new File(localPath.concat("/").concat(entry.getFilename()));
//
//
//        try {
//            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        LoggerFactory.getLogger(LOGGER_NAME).info("Successfully imported inward File: {}, start at: {}, end at: {}, total time: {}", entry.getFilename(), startTime.toString(), endTime.toString(), duration.getSeconds());


    }
}
