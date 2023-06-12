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
    public void getFiles(QuartzJobInfo jobInfo){
        Session session = null;
        ChannelSftp channelSftp = null;
        Server sourceServer = jobInfo.getSourceServer(), destinationServer = jobInfo.getDestinationServer();
        String sourcePath = jobInfo.getSourcePath(), localBasePath = configuration.getLocalFileLocation().concat("/").concat(sourceServer.getHost()).concat("/").concat(jobInfo.getSourcePath()).concat("/");

        try {
            session = Util.createSession(sourceServer.getUser(), sourceServer.getHost(), sourceServer.getPort(), sourceServer.getSecureFileName(), sourceServer.getPassword());
            channelSftp = Util.createChannelSftp(session);

            Vector<ChannelSftp.LsEntry> childFolders = channelSftp.ls(sourcePath+"/*");

            Partition<ChannelSftp.LsEntry> partition = Partition.getPartitionInstance(childFolders, 1200);
            LoggerFactory.getLogger(LOGGER_NAME).info("Total folder: {} and partition size: {}", childFolders.size(), partition.size());
            List<FileThread> tasks = new ArrayList<>();
            int i = 1;

            for(List<ChannelSftp.LsEntry> chunkFolder : partition){
                tasks.add(new FileThread("Thread-"+ i++,chunkFolder, jobInfo.getJobKey(), sourceServer, sourcePath, localBasePath, destinationServer.getHost(), jobInfo.getDestinationPath(), jobInfo.getFileExtension(), fileService));
            }

            PoolInstance poolInstance = Util.poolInstance;
            poolInstance.setTasks(tasks);
            poolInstance.implementSingleInstance();

        } catch (FileUploaderException exception){
            throw exception;
        } catch (Exception e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            Util.destroyConnection(session, channelSftp);
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
