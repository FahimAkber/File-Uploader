package com.example.fileuploader.threadConfigurer;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.Status;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.util.Util;
import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

public class FileThread implements Callable<String> {
    private String threadId;
    private List<ChannelSftp.LsEntry> folders;
    private String jobKey;
    private Server sourceServer;
    private String sourcePath;
    private String localBasePath;
    private String destinationHost;
    private String destinationPath;
    private String fileExtension;
    private final UploadedFileService uploadedFileService;
    private static final String LOGGER_NAME = "File Uploader";
    public FileThread(String threadId, List<ChannelSftp.LsEntry> folders,
                      String jobKey, Server sourceServer, String sourcePath, String localBasePath, String destinationHost, String destinationPath, String fileExtension, UploadedFileService uploadedFileService){
        this.threadId = threadId;
        this.folders = folders;
        this.jobKey = jobKey;
        this.sourceServer = sourceServer;
        this.sourcePath = sourcePath;
        this.localBasePath = localBasePath;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.fileExtension = fileExtension;
        this.uploadedFileService = uploadedFileService;
    }

    @Override
    public String call() {
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = createSession(sourceServer.getUser(), sourceServer.getHost(), sourceServer.getPort(), sourceServer.getSecureFileName(), sourceServer.getPassword());
            channelSftp = createChannelSftp(session);
            for (ChannelSftp.LsEntry folder : folders){
                channelSftp.cd(sourcePath.concat("/").concat(folder.getFilename() + "/"));
                Vector<ChannelSftp.LsEntry> files = channelSftp.ls("*."+fileExtension);
                Collections.sort(files, Collections.reverseOrder());
                ChannelSftp.LsEntry latestCompletedFile = files.stream().skip(1).findFirst().orElse(null);
                File localPath = Util.createDirIfNotExist(localBasePath.concat(folder.getFilename()));
                if(latestCompletedFile != null){
                    uploadFileToLocalDestination(latestCompletedFile.getFilename(), latestCompletedFile.getAttrs().getSize(), sourceServer.getHost(), sourcePath, localPath.getAbsolutePath(), destinationHost, destinationPath, channelSftp);
                }else{
                    //TODO: If latest file not found.
                }
            }

            return threadId + "completed at : " + Calendar.getInstance().getTime();
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(channelSftp != null){
                channelSftp.disconnect();
            }
            if(session != null){
                session.disconnect();
            }
        }
    }
    private ChannelSftp createChannelSftp(Session session) {
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

    private void uploadFileToLocalDestination(String fileName, Long actualSize, String sourceHost, String sourcePath, String localPath, String destinationHost, String destinationPath, ChannelSftp channelSftp) throws Exception {
        try {
            File localUploadedFile = new File(localPath, fileName);
            if(!localUploadedFile.exists()){
                channelSftp.get(fileName, localPath);
                Date receivingCompleted = Calendar.getInstance().getTime();
                Long uploadedFileSize =  localUploadedFile.length();
                if(!localUploadedFile.exists()){
                    //TODO: insert to error db
                    throw new Exception(fileName + " didn't fetch");
                }
                if(uploadedFileSize < actualSize){
                    //TODO: insert to error db
                    throw new Exception(fileName + " didn't fetch properly. Actual Size: "+ actualSize + " and Uploaded File Size: " + uploadedFileSize);
                }
                UploadedFile uploadedFile = new UploadedFile(fileName, sourceHost, sourcePath, actualSize, receivingCompleted, localPath, uploadedFileSize, destinationHost, destinationPath, Status.RECEIVED.value);
                uploadedFileService.save(uploadedFile);
                LoggerFactory.getLogger(LOGGER_NAME).info("Successfully imported to local - File: {}", fileName);
            }
            else{
                UploadedFile uploadedFile = new UploadedFile(fileName, sourceHost, sourcePath, actualSize, localPath, destinationHost, destinationPath, Status.RECEIVED.value, "File already exists in local path");
                uploadedFileService.save(uploadedFile);
                LoggerFactory.getLogger(LOGGER_NAME).info("Already Exist in local - File: {}", fileName);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(LOGGER_NAME).error(e.getMessage());
        }
    }

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

}
