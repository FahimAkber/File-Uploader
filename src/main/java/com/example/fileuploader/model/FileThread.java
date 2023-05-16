package com.example.fileuploader.model;

import com.example.fileuploader.configuration.FIleProgressMonitor;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class FileThread implements Runnable{

    private ChannelSftp channelSftp;
    private ChannelSftp.LsEntry entry;
    private String path;
    private List<ChannelSftp.LsEntry> lsEntries;
    private Session session;
    private String remotePath;


    public FileThread(Session session, List<ChannelSftp.LsEntry> lsEntries, String path, String remotePath) {
        this.session = session;
        this.lsEntries = lsEntries;
        this.path = path;
        this.channelSftp = createChannelSftp(session);
        this.remotePath = remotePath;
    }


    @Override
    public void run() {
        try {
            channelSftp.cd(remotePath);
            for (ChannelSftp.LsEntry entry : lsEntries) {
                uploadFileToLocalDestination(entry);
            }
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } finally {
            destroyConnection(session, channelSftp);
        }
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
    private void uploadFileToLocalDestination() throws SftpException {
        channelSftp.get(entry.getFilename(), path, new FIleProgressMonitor());
        LoggerFactory.getLogger("File Uploader").info("Successfully imported inward File: {}", entry.getFilename());
    }
    private synchronized void uploadFileToLocalDestination(ChannelSftp.LsEntry entry) throws SftpException {
        LocalDateTime startTime = LocalDateTime.now();
        channelSftp.get(entry.getFilename(), path);

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        LoggerFactory.getLogger("File Uploader").info("Successfully imported inward File: {}, start at: {}, end at: {}, total time: {}", entry.getFilename(), startTime.toString(), endTime.toString(), duration.getSeconds());


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
}
