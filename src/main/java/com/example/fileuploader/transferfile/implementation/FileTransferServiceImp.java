package com.example.fileuploader.transferfile.implementation;

import com.example.fileuploader.configuration.Partition;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.gateway.CallApi;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.threadConfigurer.ThreadInfo;
import com.example.fileuploader.transferfile.FileTransferService;
import com.example.fileuploader.util.Util;
import com.jcraft.jsch.*;
import com.example.fileuploader.model.Configuration;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileTransferServiceImp implements FileTransferService {

    private final UploadedFileService fileService;
    private Configuration configuration;
    private Queue<String> jobQueue;

    private final CallApi callApi;
    private static final String LOGGER_NAME = "File Uploader";

    public FileTransferServiceImp(UploadedFileService fileService, Configuration configuration, CallApi callApi) {
        this.fileService = fileService;
        this.configuration = configuration;
        this.callApi = callApi;
        jobQueue = new LinkedList<>();
    }
    public void getFiles(QuartzJobInfo jobInfo){
        Session session = null;
        ChannelSftp channelSftp = null;

        Server sourceServer = jobInfo.getSourceServer(),
               destinationServer = jobInfo.getDestinationServer();

        String sourceHost = sourceServer.getHost(),
               sourceUser = sourceServer.getUser(),
               sourceFile = sourceServer.getSecureFileName(),
               sourcePassword = sourceServer.getPassword();

        int sourcePort = sourceServer.getPort();

        String sourcePath = jobInfo.getSourcePath(),
               localBasePath = configuration.getLocalFileLocation().concat("/")
                                            .concat(sourceServer.getHost()).concat("/")
                                            .concat(jobInfo.getSourcePath()).concat("/");

        try {
            session = Util.createSession(sourceUser, sourceHost, sourcePort, sourceFile, sourcePassword);
            channelSftp = Util.createChannelSftp(session);

            Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(sourcePath+"/*");
            List<String> childFolders = entries.stream().map(ChannelSftp.LsEntry::getFilename).collect(Collectors.toList());

            Partition<String> partition = Partition.getPartitionInstance(childFolders, 1000);
            LoggerFactory.getLogger(LOGGER_NAME).info("Total folder: {} and partition size: {}", childFolders.size(), partition.size());
            List<ThreadInfo> tasks1 = new ArrayList<>();
            List<ThreadInfo> tasks2 = new ArrayList<>();


            for(int i = 0; i < partition.size(); i++){
                ThreadInfo threadInfo = new ThreadInfo("Thread-" + (i+1), partition.get(i), sourceHost, sourcePort, sourceUser, sourcePassword, sourceFile, sourcePath, localBasePath, destinationServer.getHost(), jobInfo.getDestinationPath(), jobInfo.getFileExtension());
                if(i%2 == 0){
                    tasks2.add(threadInfo);
                }else {
                    tasks1.add(threadInfo);
                }
            }
            callApi.collectFiles(tasks1, "http://localhost:8088/file/collect");
            callApi.collectFiles(tasks2, "http://localhost:8088/file/collect");

            //pass to object

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
