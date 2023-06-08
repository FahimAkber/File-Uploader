package com.example.fileuploader.threadConfigurer;

import com.example.fileuploader.model.Status;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.util.Util;
import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class FileThread implements Runnable{

    private ChannelSftp channelSftp;
    private List<ChannelSftp.LsEntry> folders;
    private String jobKey;
    private String sourceHost;
    private String sourcePath;
    private String localBasePath;
    private String destinationHost;
    private String destinationPath;
    private String fileExtension;
    private final UploadedFileService uploadedFileService;
    private static final String LOGGER_NAME = "File Uploader";
    public FileThread(ChannelSftp channelSftp, List<ChannelSftp.LsEntry> folders,
                      String jobKey, String sourceHost, String sourcePath, String localBasePath, String destinationHost, String destinationPath, String fileExtension, UploadedFileService uploadedFileService){
        this.channelSftp = channelSftp;
        this.folders = folders;
        this.jobKey = jobKey;
        this.sourceHost = sourceHost;
        this.sourcePath = sourcePath;
        this.localBasePath = localBasePath;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.fileExtension = fileExtension;
        this.uploadedFileService = uploadedFileService;
    }

    @Override
    public void run() {
        try {
            for (ChannelSftp.LsEntry folder : folders){
                channelSftp.cd(sourcePath.concat("/").concat(folder.getFilename() + "/"));
                Vector<ChannelSftp.LsEntry> files = channelSftp.ls("*."+fileExtension);
                Collections.sort(files, Collections.reverseOrder());
                ChannelSftp.LsEntry latestCompletedFile = files.stream().skip(1).findFirst().orElse(null);
                File localPath = Util.createDirIfNotExist(localBasePath.concat(folder.getFilename()));
                if(latestCompletedFile != null){
                    uploadFileToLocalDestination(latestCompletedFile.getFilename(), latestCompletedFile.getAttrs().getSize(), sourceHost, sourcePath, localPath.getAbsolutePath(), destinationHost, destinationPath, channelSftp);
                }else{
                    //TODO: If latest file not found.
                }

            }
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

}
