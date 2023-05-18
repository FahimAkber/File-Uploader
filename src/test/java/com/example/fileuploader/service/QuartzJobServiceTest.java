//package com.example.fileuploader.service;
//
//import com.example.fileuploader.exceptions.FileUploaderException;
//import com.example.fileuploader.quartzscheduler.implementaion.QuartzSchedulerServiceImpl;
//import com.example.fileuploader.repository.QuartzJobInfoRepository;
//import com.example.fileuploader.model.JobInfo;
//import com.example.fileuploader.model.JobRequest;
//import com.example.fileuploader.model.RequestTrigger;
//import com.sun.org.apache.regexp.internal.RE;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.modelmapper.ModelMapper;
//import org.quartz.*;
//
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class QuartzJobServiceTest {
//
//    @Mock
//    JobRequest job;
//
//    @Mock
//    RequestTrigger requestTrigger;
//
//    @Mock
//    JobDetail jobDetail;
//
//    @Mock
//    Date date;
//
//    @Mock
//    Trigger trigger;
//
//    @Mock
//    TriggerBuilder builder;
//
//    @Mock
//    SimpleTrigger simpleTrigger;
//
//    @Mock
//    JobInfo info;
//
//    @Mock
//    Scheduler scheduler;
//
//    @Mock
//    QuartzJobInfoService jobInfoService;
//
//    @Mock
//    QuartzJobInfoRepository repository;
//
//    @Mock
//    ModelMapper modelMapper;
//
//    @Spy
//    @InjectMocks
//    QuartzSchedulerServiceImpl quartzSchedulerService;
//
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void notNull(){
//        Assertions.assertNotNull(quartzSchedulerService);
//    }
//
//    @Test
//    void saveJob() throws SchedulerException {
//        JobRequest jobRequest = new JobRequest("Inward File Uploader", "File import from PBM simultaneously.", 3);
//
//        Mockito.when(job.getJobKey()).thenReturn("Inward File Uploader");
//        Mockito.when(job.getJobDescription()).thenReturn("File import from PBM simultaneously.");
//        Mockito.when(job.getJobInformationId()).thenReturn(3);
//        Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("Inward File Uploader"));
//        Mockito.when(jobInfoService.getQuartzJobInfoById(job.getJobInformationId())).thenReturn(new JobInfo());
//        Mockito.when(scheduler.checkExists(jobDetail.getKey())).thenReturn(true);
//
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.saveJob(job);
//        });
//
//    }
//
//    @Test
//    void saveJobForFalse() throws SchedulerException {
//        JobRequest jobRequest = new JobRequest("Inward File Uploader", "File import from PBM simultaneously.", 3);
//
//        Mockito.when(job.getJobKey()).thenReturn("Inward File Uploader");
//        Mockito.when(job.getJobDescription()).thenReturn("File import from PBM simultaneously.");
//        Mockito.when(job.getJobInformationId()).thenReturn(3);
//        Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("Inward File Uploader"));
//        Mockito.when(jobInfoService.getQuartzJobInfoById(job.getJobInformationId())).thenReturn(new JobInfo());
//        Mockito.when(scheduler.checkExists(jobDetail.getKey())).thenReturn(false);
//
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.saveJob(job);
//        });
//
//    }
//
//    @Test
//    void saveJobForError() throws SchedulerException {
//        JobRequest jobRequest = new JobRequest("Test Key", "Test Description", 1);
//
//        Mockito.when(job.getJobKey()).thenReturn("Inward File Uploader");
//        Mockito.when(job.getJobDescription()).thenReturn("File import from PBM simultaneously.");
//        Mockito.when(job.getJobInformationId()).thenReturn(3);
//        Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("Inward File Uploader"));
//        Mockito.when(jobInfoService.getQuartzJobInfoById(job.getJobInformationId())).thenReturn(new JobInfo());
//        Mockito.when(scheduler.checkExists(jobDetail.getKey())).thenThrow(SchedulerException.class);
//        assertThrows(FileUploaderException.class, ()->{
//            quartzSchedulerService.saveJob(job);
//        });
//
//    }
//
//    @Test
//    void saveJobTestForJobkey(){
//        Mockito.when(job.getJobKey()).thenReturn(null);
//
//
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.saveJob(job);
//        });
//
//        String expected = "Job key can't be null";
//        String actual = exception.getErrorMessage();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveJobTestForJobDescription(){
//        Mockito.when(job.getJobKey()).thenReturn("123");
//        Mockito.when(job.getJobDescription()).thenReturn(null);
//
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.saveJob(job);
//        });
//
//        String expected = "Job Description can't be null";
//        String actual = exception.getErrorMessage();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveJobTestForQuartzJobInfo(){
//        Mockito.when(job.getJobKey()).thenReturn("234");
//        Mockito.when(job.getJobDescription()).thenReturn("description");
//        Mockito.when(job.getJobInformationId()).thenReturn(0);
//        Mockito.when(jobInfoService.getQuartzJobInfoById(job.getJobInformationId())).thenReturn(null);
//
//
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.saveJob(job);
//        });
//
//        String expected = "No job information available with the job id: 0";
//        String actual = exception.getErrorMessage();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveTriggerTestForJob(){
//        Mockito.when(requestTrigger.getJob()).thenReturn(null);
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.saveTrigger(requestTrigger);
//        });
//        String actual = exception.getErrorMessage();
//        String expected = "Must to provide specific job";
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    void saveTrigger(){
//        Mockito.when(requestTrigger.getJob()).thenReturn(jobDetail);
//        Mockito.when(requestTrigger.getJob().getKey()).thenReturn(new JobKey("test"));
//        Mockito.when(requestTrigger.getJob().getDescription()).thenReturn("test");
//        Mockito.when(requestTrigger.getStartAt()).thenReturn(null);
//        Mockito.when(requestTrigger.getTotalInterval()).thenReturn(0);
//        Mockito.when(requestTrigger.getFrequency()).thenReturn(10000);
//
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.saveTrigger(requestTrigger);
//        });
//    }
//
//    @Test
//    void saveTriggerUpdateData(){
//        Date date = Calendar.getInstance().getTime();
//        Mockito.when(requestTrigger.getJob()).thenReturn(jobDetail);
//        Mockito.when(requestTrigger.getJob().getKey()).thenReturn(new JobKey("test"));
//        Mockito.when(requestTrigger.getJob().getDescription()).thenReturn("test");
//        Mockito.when(requestTrigger.getStartAt()).thenReturn(date);
//        Mockito.when(requestTrigger.getTotalInterval()).thenReturn(1);
//        Mockito.when(requestTrigger.getFrequency()).thenReturn(10000);
//
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.saveTrigger(requestTrigger);
//        });
//    }
//
//    @Test
//    void saveTriggerForError() throws SchedulerException {
//        Mockito.when(requestTrigger.getJob()).thenReturn(jobDetail);
//        Mockito.when(requestTrigger.getJob().getKey()).thenReturn(new JobKey("test"));
//        Mockito.when(requestTrigger.getJob().getDescription()).thenReturn("test");
//        Mockito.when(requestTrigger.getStartAt()).thenReturn(null);
//        Mockito.when(requestTrigger.getTotalInterval()).thenReturn(0);
//        Mockito.when(requestTrigger.getFrequency()).thenReturn(10000);
//        Mockito.when(builder.build()).thenReturn(simpleTrigger);
//        Mockito.when(scheduler.scheduleJob(any())).thenThrow(SchedulerException.class);
//        assertThrows(FileUploaderException.class, ()->{
//            quartzSchedulerService.saveTrigger(requestTrigger);
//        });
//    }
//
//    @Test
//    void resumeJob() throws SchedulerException {
//        String key = "Outward Test Key";
//        JobKey jobKey = new JobKey(key);
//        Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
//        Mockito.when(scheduler.getTriggersOfJob(jobKey)).thenReturn(new ArrayList<>());
//
//
//        assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.resumeJob(jobKey);
//        });
//
//    }
//
//    @Test
//    void pauseJob(){
//        JobKey key = new JobKey("Test");
//        assertDoesNotThrow(()->quartzSchedulerService.pauseJob(key));
//    }
//
//    @Test
//    void pauseJobException(){
//        assertThrows(FileUploaderException.class, ()->{
//            quartzSchedulerService.pauseJob(null);
//        });
//    }
//
//    @Test
//    void cancelTrigger() throws SchedulerException {
//       Mockito.when(scheduler.getTriggersOfJob(new JobKey("test"))).thenReturn(new ArrayList<>());
//       assertDoesNotThrow(()->{
//           quartzSchedulerService.cancelTrigger(new JobKey("test"));
//       });
//    }
//
//    @Test
//    void updateJob(){
//        JobRequest job = new JobRequest("Test key", "Test Description", 0);
//        Mockito.when(jobInfoService.getQuartzJobInfoById(0)).thenReturn(null);
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.updateJob(job);
//        });
//    }
//
//    @Test
//    void updateJobForError() throws SchedulerException {
//        //RequestJob job = new RequestJob("Inward File Uploader", "File import from PBM simultaneously.", 3);
//
//        Mockito.when(job.getJobKey()).thenReturn("Inward File Uploader");
//        Mockito.when(job.getJobDescription()).thenReturn("File import from PBM simultaneously.");
//        Mockito.when(job.getJobInformationId()).thenReturn(3);
//        Mockito.when(jobDetail.getKey()).thenReturn(new JobKey("Inward File Uploader"));
//        Mockito.when(jobInfoService.getQuartzJobInfoById(job.getJobInformationId())).thenReturn(new JobInfo());
//        Mockito.when(scheduler.checkExists(jobDetail.getKey())).thenThrow(SchedulerException.class);
//        Mockito.doThrow(SchedulerException.class).when(scheduler).addJob(jobDetail, true);
//        assertThrows(FileUploaderException.class, ()->{
//            quartzSchedulerService.updateJob(job);
//        });
//    }
//
//    @Test
//    void getJobByKey() throws SchedulerException {
//        JobKey jobKey = new JobKey("test key");
//        Mockito.when(scheduler.getJobDetail(jobKey)).thenReturn(null);
//
//        assertDoesNotThrow(()->{
//            quartzSchedulerService.getJobByKey(jobKey);
//        });
//    }
//    @Test
//    void getJobByKeyForError() throws SchedulerException {
//        JobKey jobKey = new JobKey("test key");
//        Mockito.when(scheduler.getJobDetail(jobKey)).thenThrow(new SchedulerException());
//        FileUploaderException exception = assertThrows(FileUploaderException.class, () -> {
//            quartzSchedulerService.getJobByKey(jobKey);
//        });
//        System.out.println(exception.getErrorMessage());
//    }
//}