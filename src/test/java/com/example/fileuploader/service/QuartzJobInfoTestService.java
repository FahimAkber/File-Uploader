//package com.example.fileuploader.service;
//
//import com.example.fileuploader.exceptions.FileUploaderException;
//import com.example.fileuploader.model.entities.QuartzJobInfo;
//import com.example.fileuploader.repository.QuartzJobInfoRepository;
//import com.example.fileuploader.service.implementation.QuartzJobInfoServiceImpl;
//import com.example.fileuploader.model.JobInfo;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Date;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class QuartzJobInfoTestService {
//
//    @Spy
//    @InjectMocks
//    private QuartzJobInfoServiceImpl quartzJobInfoService;
//
//    @Mock
//    QuartzJobInfoRepository quartzJobInfoRepository;
//    @Mock
//    ModelMapper modelMapper;
//    @Mock
//    JobInfo job;
//    @Mock
//    QuartzJobInfo quartzJobInfo;
//
//
//    @Test
//    void saveQuartzJobForJobType(){
//        Mockito.when(job.getJobType()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Job type cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForOperationType(){
//        Mockito.when(job.getJobType()).thenReturn("jobType");
//        Mockito.when(job.getOperationType()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Operation type cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForLocalExtension(){
//        Mockito.when(job.getJobType()).thenReturn("jobType");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Local extension cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForRemoteExtension(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Remote extension cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForProjectName(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Project name cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForRemoteHost(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Remote host cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForRemoteUser(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn("null");
//        Mockito.when(job.getRemoteUser()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Remote user cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForRemotePassword(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn("null");
//        Mockito.when(job.getRemoteUser()).thenReturn("null");
//        Mockito.when(job.getRemotePassword()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Remote password cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForRemotePath(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn("null");
//        Mockito.when(job.getRemoteUser()).thenReturn("null");
//        Mockito.when(job.getRemotePassword()).thenReturn("null");
//        Mockito.when(job.getRemotePath()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Remote path cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJobForLocalPath(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn("null");
//        Mockito.when(job.getRemoteUser()).thenReturn("null");
//        Mockito.when(job.getRemotePassword()).thenReturn("null");
//        Mockito.when(job.getRemotePath()).thenReturn("null");
//        Mockito.when(job.getLocalPath()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//
//        String expected = "Local path cannot be null or empty String";
//        String actual = exception.getErrorMessage();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveQuartzJob(){
//        Mockito.when(job.getJobType()).thenReturn("null");
//        Mockito.when(job.getOperationType()).thenReturn("null");
//        Mockito.when(job.getLocalExtension()).thenReturn("null");
//        Mockito.when(job.getRemoteExtension()).thenReturn("null");
//        Mockito.when(job.getProjectName()).thenReturn("null");
//        Mockito.when(job.getRemoteHost()).thenReturn("null");
//        Mockito.when(job.getRemoteUser()).thenReturn("null");
//        Mockito.when(job.getRemotePassword()).thenReturn("null");
//        Mockito.when(job.getRemotePath()).thenReturn("null");
//        Mockito.when(job.getLocalPath()).thenReturn("null");
//        Mockito.doNothing().when(quartzJobInfo).setCreatedBy("a");
//        Mockito.doNothing().when(quartzJobInfo).setExecutedAt(new Date());
//
//        assertDoesNotThrow(()->{
//            quartzJobInfoService.saveQuartzJob(job);
//        });
//    }
//
////    @Test
////    void getQuartzJobById(){
//////        QuartzJobInfo quartzJobInfo = quartzJobInfoRepository.findById(3).get();
////        Mockito.when(quartzJobInfoRepository.findById(3).get()).thenThrow((new Exception()));
//////        Mockito.when(modelMapper.map(quartzJobInfoRepository.findById(3).get(), JobInfo.class).getLocalPath())
//////                .thenReturn(null);
////
////        assertDoesNotThrow(()->{quartzJobInfoService.getQuartzJobInfoById(3);});
////    }
//}
