//package com.example.fileuploader.service;
//
//import com.example.fileuploader.configuration.Partition;
//import com.example.fileuploader.exceptions.FileUploaderException;
//import com.example.fileuploader.model.entities.QuartzJobInfo;
//import com.example.fileuploader.repository.QuartzJobInfoRepository;
//import com.example.fileuploader.repository.UploadedFileRepository;
//import com.example.fileuploader.service.implementation.QuartzJobInfoServiceImpl;
//import com.example.fileuploader.transferfile.implementation.FileTransferServiceImp;
//import com.jcraft.jsch.*;
//import com.example.fileuploader.model.Configuration;
//import com.example.fileuploader.model.JobInfo;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//
//import javax.persistence.EntityManager;
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//@SpringBootTest
//public class FileTransferServiceTest {
//
//    @Spy
//    @InjectMocks
//    FileTransferServiceImp fileTransferServiceImp;
//
//    @Mock
//    QuartzJobInfoServiceImpl quartzJobInfoService;
//
//    @Mock
//    QuartzJobInfoRepository repository;
//    @Mock
//    UploadedFileService uploadedFileService;
//
//    @Mock
//    ModelMapper modelMapper;
//
//    @Mock
//    QuartzJobInfo quartzJobInfo;
//
//    @Mock
//    UploadedFileRepository uploadedFileRepository;
//
//    @Mock
//    Configuration configuration;
//
//    @Mock
//    JobInfo jobInfo;
//
//    @Mock
//    JSch jSch;
//
//    @Mock
//    Queue<String> queue;
//
//    @Mock
//    Session session;
//
//    @Mock
//    ChannelSftp channelSftp;
//
//    @Mock
//    Partition partition;
//
//    @Mock
//    File file;
//
//    @Mock
//    SimpleDateFormat sdf;
//
//    @Mock
//    private EntityManager entityManager;
//
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void notnull(){
//        Assertions.assertNotNull(fileTransferServiceImp);
//    }
//
//    @Test
//    void createSession() throws JSchException {
//        String configurationKey = configuration.getConfigurationKey();
//        String configurationValue = configuration.getConfigurationValue();
//        Mockito.when(configuration.getConfigurationKey()).thenReturn("null");
//        Mockito.when(configuration.getConfigurationValue()).thenReturn("null");
//        Mockito.doNothing().when(session).setConfig(configurationKey, configurationValue);
//        Mockito.doNothing().when(session).setPassword("pass");
//        Mockito.doNothing().when(session).connect();
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () ->
//            fileTransferServiceImp.createSession("Arafat", "arnayema", "172.16.215.85", 22)
//        );
//
//        String expected = exception.getErrorMessage();
//        System.out.println(expected);
//        String actual = exception.getErrorMessage();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void createSessionForJschException() throws JSchException {
//
//        Mockito.doThrow(JSchException.class).when(jSch).getSession("Arafat", "127.16.215.85", 22);
////        Mockito.doNothing().when(session).setConfig(configurationKey, configurationValue);
////        Mockito.doNothing().when(session).setPassword("pass");
//        Mockito.doThrow(JSchException.class).when(session).connect();
////        //
////
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () ->
//                fileTransferServiceImp.createSession("Arafat", "arnayema", "172.16.215.85", 22)
//        );
//        String expected = exception.getErrorMessage();
//        System.out.println(expected);
//        String actual = exception.getErrorMessage();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void createChannelSftp() throws JSchException {
//        Mockito.when(configuration.getChannelType()).thenReturn("test");
//        Mockito.when(session.openChannel(configuration.getChannelType())).thenReturn(null);
//        Mockito.doThrow(FileUploaderException.class).when(channelSftp).connect();
//        assertThrows(FileUploaderException.class, ()->
//            fileTransferServiceImp.createChannelSftp(session)
//        );
//        Mockito.verify(fileTransferServiceImp, Mockito.atLeastOnce()).createChannelSftp(session);
//    }
//
//    @Test
//    void setUpJsch() {
//        Mockito.when(configuration.getConfigurationKey()).thenReturn("StrictHostKeyChecking");
//        Mockito.when(configuration.getConfigurationValue()).thenReturn("no");
//        Mockito.when(configuration.getChannelType()).thenReturn("sftp");
//        Mockito.doThrow(new FileUploaderException("Exception", HttpStatus.NOT_FOUND)).when(fileTransferServiceImp).createSession("Arafat", "arnayema", "172.16.215.85", 22);
//        FileUploaderException exception = assertThrows(
//                FileUploaderException.class, () ->{
//                   fileTransferServiceImp.createSession("Arafat", "arnayema", "172.16.215.85", 22);
//                });
//
//        String expected = "Exception";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//
//    }
//
//    @Test
//    void getFilesTestForPreviousJobRunning(){
//        Mockito.when(jobInfo.getJobType()).thenReturn("a");
//        Mockito.when(queue.contains(jobInfo.getJobType())).thenReturn(true);
//
//        assertDoesNotThrow(()->{
//            fileTransferServiceImp.getFiles(jobInfo);
//        });
//    }
//
//
//    @Test
//    void getFiles() throws JSchException {
//
//        String jobType= "Outward";
//        String localExtension= "Outward";
//        String operationType= "EXPORT";
//        String remoteExtension= "Inward";
//        String localPath= "/C:/Users/Fahim Mahmud/Documents/upload/";
//        String projectName= "BEFTN";
//        String host= "172.16.215.85";
//        String pass= "arnayema";
//        String remotePath= "/C:/Users/Arafat/Downloads/New folder/";
//        int port= 22;
//        String user= "Arafat";
//
//        String configurationKey = configuration.getConfigurationKey();
//        String configurationValue = configuration.getConfigurationValue();
//
//        Mockito.when(jobInfo.getJobType()).thenReturn(jobType);
//        Mockito.when(jobInfo.getLocalExtension()).thenReturn(localExtension);
//        Mockito.when(jobInfo.getOperationType()).thenReturn(operationType);
//        Mockito.when(jobInfo.getRemoteExtension()).thenReturn(remoteExtension);
//        Mockito.when(jobInfo.getLocalPath()).thenReturn(localPath);
//        Mockito.when(jobInfo.getProjectName()).thenReturn(projectName);
//        Mockito.when(jobInfo.getRemoteHost()).thenReturn(host);
//        Mockito.when(jobInfo.getRemotePassword()).thenReturn(pass);
//        Mockito.when(jobInfo.getRemotePath()).thenReturn(remotePath);
//        Mockito.when(jobInfo.getRemoteUser()).thenReturn(user);
//        Mockito.when(jobInfo.getRemotePort()).thenReturn(port);
//
//        Mockito.when(jSch.getSession(user, host, port)).thenReturn(session);
//        Mockito.when(configuration.getConfigurationKey()).thenReturn(configurationKey);
//        Mockito.when(configuration.getConfigurationValue()).thenReturn(configurationValue);
//        Mockito.when(configuration.getChannelType()).thenReturn("sftp");
//        Mockito.doNothing().when(session).setConfig(configurationKey, configurationValue);
//        Mockito.doNothing().when(session).setPassword(pass);
//        String channelType = configuration.getChannelType();
//        Mockito.doReturn(channelSftp).when(session).openChannel(channelType);
//        Mockito.doNothing().when(channelSftp).connect();
//
//        List<Object> files = new ArrayList<>();
//        Partition<Object> partitionInstance = Partition.getPartitionInstance(files, 500);
////        Mockito.when(Partition.getPartitionInstance(files, 500)).thenReturn(partitionInstance);
//
//
//        assertThrows(FileUploaderException.class, ()->{
//            fileTransferServiceImp.getFiles(jobInfo);
//        });
//    }
//
//
////    @Test
////    void getFilesForLoop() throws JSchException, SftpException {
////        String jobType= "Outward";
////        String localExtension= "Outward";
////        String operationType= "EXPORT";
////        String remoteExtension= "Inward";
////        String localPath= "/C:/Users/Fahim Mahmud/Documents/upload/";
////        String projectName= "BEFTN";
////        String host= "172.16.215.85";
////        String pass= "arnayema";
////        String remotePath= "/C:/Users/Arafat/Downloads/New folder/";
////        int port= 22;
////        String user= "Arafat";
////
////        String configurationKey = configuration.getConfigurationKey();
////        String configurationValue = configuration.getConfigurationValue();
////
////        Mockito.when(jobInfo.getJobType()).thenReturn(jobType);
////        Mockito.when(jobInfo.getLocalExtension()).thenReturn(localExtension);
////        Mockito.when(jobInfo.getOperationType()).thenReturn(operationType);
////        Mockito.when(jobInfo.getRemoteExtension()).thenReturn(remoteExtension);
////        Mockito.when(jobInfo.getLocalPath()).thenReturn(localPath);
////        Mockito.when(jobInfo.getProjectName()).thenReturn(projectName);
////        Mockito.when(jobInfo.getRemoteHost()).thenReturn(host);
////        Mockito.when(jobInfo.getRemotePassword()).thenReturn(pass);
////        Mockito.when(jobInfo.getRemotePath()).thenReturn(remotePath);
////        Mockito.when(jobInfo.getRemoteUser()).thenReturn(user);
////        Mockito.when(jobInfo.getRemotePort()).thenReturn(port);
////        Mockito.when(jSch.getSession(user, host, port)).thenReturn(session);
////        Mockito.when(configuration.getConfigurationKey()).thenReturn(configurationKey);
////        Mockito.when(configuration.getConfigurationValue()).thenReturn(configurationValue);
////        Mockito.when(configuration.getChannelType()).thenReturn("sftp");
////        assertDoesNotThrow(()-> fileTransferServiceImp.getFiles(jobInfo));
////
////    }
//
//    private void configurationProperties() {
//        Mockito.when(jobInfo.getRemoteUser()).thenReturn("Arafat");
//        Mockito.when(jobInfo.getRemotePassword()).thenReturn("arnayema");
//        Mockito.when(jobInfo.getRemotePort()).thenReturn(22);
//        Mockito.when(jobInfo.getProjectName()).thenReturn("BEFTN");
//        Mockito.when(jobInfo.getLocalPath()).thenReturn("/C:/Users/Fahim Mahmud/Documents/upload/");
//        Mockito.when(jobInfo.getRemotePath()).thenReturn("/C:/Users/Arafat/Downloads/New folder/");
//        Mockito.when(jobInfo.getLocalExtension()).thenReturn("Inward");
//        Mockito.when(jobInfo.getRemoteExtension()).thenReturn("Outward");
//        Mockito.when(jobInfo.getRemoteHost()).thenReturn("172.16.215.85");
//        Mockito.when(configuration.getConfigurationKey()).thenReturn("StrictHostKeyChecking");
//        Mockito.when(configuration.getConfigurationValue()).thenReturn("no");
//        Mockito.when(configuration.getChannelType()).thenReturn("sftp");
//    }
//
//    @Test
//    void setFiles(){
//        configurationProperties();
//        Mockito.doNothing().when(fileTransferServiceImp).setFiles(jobInfo);
//        assertDoesNotThrow(()-> fileTransferServiceImp.setFiles(jobInfo));
//        Mockito.verify(fileTransferServiceImp, Mockito.atLeastOnce()).setFiles(jobInfo);
////        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
////            fileTransferServiceImp.setFiles(jobInfo);
////        });
////
////        String actual = "java.net.ConnectException: Connection timed out: connect";
////        String expected = exception.getErrorMessage();
////
////        assertEquals(expected, actual);
//    }
//
//    @Test
//    void setFilesForException() throws JSchException {
//        String jobType= "Outward";
//        String localExtension= "Outward";
//        String operationType= "EXPORT";
//        String remoteExtension= "Inward";
//        String localPath= "/C:/Users/Fahim Mahmud/Documents/upload/";
//        String projectName= "BEFTN";
//        String host= "172.16.215.85";
//        String pass= "arnayemaaaa";
//        String remotePath= "/C:/Users/Arafat/Downloads/New folder/";
//        int port= 22;
//        String user= "Arafat";
//
//        String configurationKey = configuration.getConfigurationKey();
//        String configurationValue = configuration.getConfigurationValue();
//
//        Mockito.when(jobInfo.getJobType()).thenReturn(jobType);
//        Mockito.when(jobInfo.getLocalExtension()).thenReturn(localExtension);
//        Mockito.when(jobInfo.getOperationType()).thenReturn(operationType);
//        Mockito.when(jobInfo.getRemoteExtension()).thenReturn(remoteExtension);
//        Mockito.when(jobInfo.getLocalPath()).thenReturn(localPath);
//        Mockito.when(jobInfo.getProjectName()).thenReturn(projectName);
//        Mockito.when(jobInfo.getRemoteHost()).thenReturn(host);
//        Mockito.when(jobInfo.getRemotePassword()).thenReturn(pass);
//        Mockito.when(jobInfo.getRemotePath()).thenReturn(remotePath);
//        Mockito.when(jobInfo.getRemoteUser()).thenReturn(user);
//        org.mockito.Mockito.when(jobInfo.getRemotePort()).thenReturn(port);
//
//        Mockito.when(jSch.getSession(user, host, port)).thenReturn(session);
//        Mockito.when(configuration.getConfigurationKey()).thenReturn(configurationKey);
//        Mockito.when(configuration.getConfigurationValue()).thenReturn(configurationValue);
//        Mockito.when(configuration.getChannelType()).thenReturn("sftp");
//        Mockito.doNothing().when(session).setConfig(configurationKey, configurationValue);
//        Mockito.doNothing().when(session).setPassword(pass);
//        String channelType = configuration.getChannelType();
//        Mockito.doReturn(channelSftp).when(session).openChannel(channelType);
//        Mockito.doNothing().when(channelSftp).connect();
//
//        List<Object> files = new ArrayList<>();
//        Partition<Object> partitionInstance = Partition.getPartitionInstance(files, 500);
////        Mockito.when(Partition.getPartitionInstance(files, 500)).thenReturn(partitionInstance);
//
//
//        assertDoesNotThrow(()->{
//            fileTransferServiceImp.setFiles(jobInfo);
//        });
//    }
//
////    @Test
////    void uploadFileToDestination() throws SftpException {
////
////        //Access Modifier should be changed to public when need to test.
////        Mockito.doThrow(SftpException.class).when(channelSftp).get("sourcePath");
////        assertThrows(NullPointerException.class, ()-> fileTransferServiceImp.uploadFileToLocalDestination(null, "this", channelSftp));
////        Mockito.verify(fileTransferServiceImp, Mockito.atLeastOnce()).uploadFileToLocalDestination(null, "this", channelSftp);
////    }
//
//    @Test
//    void createDirIfNotExist(){
//        String rootPath = "a";
//        String projectName = "test";
//        String extension = "b";
//        File afile = new File(rootPath.concat("/").concat(projectName).concat("/").concat(extension));
//        Mockito.when(file.exists()).thenReturn(true);
//
//        assertEquals("a\\test\\b", afile.getPath());
//    }
//
//
//
//}
