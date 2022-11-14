package com.example.fileuploader.controller;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.quartzscheduler.implementaion.QuartzSchedulerServiceImpl;
import com.example.fileuploader.service.implementation.QuartzJobInfoServiceImpl;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.RequestJob;
import com.example.fileuploader.model.RequestTrigger;
import com.example.fileuploader.model.SchedulerJobDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;


class FileUploaderTestController {

    @Spy
    @InjectMocks
    FileTransferController controller;

    @Mock
    QuartzJobInfoServiceImpl jobInfoService;

    @Mock
    QuartzSchedulerServiceImpl schedulerService;

    @Mock
    SchedulerJobDTO jobDTO;

    @Mock
    RequestTrigger trigger;

    @Mock
    JobDetail jobDetail;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void schedulingFile(){
        Mockito.when(jobDTO.getJobKey()).thenReturn("test");
        Mockito.when(jobDTO.getRequestTrigger()).thenReturn(trigger);
        Mockito.when(schedulerService.getJobByKey(new JobKey("test"))).thenReturn(jobDetail);

        Mockito.doNothing().when(trigger).setJob(jobDetail);
        Mockito.doNothing().when(schedulerService).saveTrigger(trigger);
        assertDoesNotThrow(()->{
            controller.schedulingFile(jobDTO);
        });
    }

    @Test
    void schedulingFileForException(){
        Mockito.when(jobDTO.getJobKey()).thenReturn("test");
        Mockito.when(jobDTO.getRequestTrigger()).thenReturn(trigger);
        Mockito.when(schedulerService.getJobByKey(new JobKey("test"))).thenReturn(jobDetail);

        Mockito.doNothing().when(trigger).setJob(jobDetail);
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).saveTrigger(trigger);
        ResponseEntity<Object> objectResponse = controller.schedulingFile(jobDTO);
        assertEquals("exception", objectResponse.getBody());
    }

    @Test
    void saveJobInfo(){
        JobInfo jobInfo = new JobInfo();
        Mockito.doNothing().when(jobInfoService).saveQuartzJob(jobInfo);
        assertDoesNotThrow(()->{
            controller.saveJobInfo(jobInfo);
        });
    }

    @Test
    void saveJobInfoForException(){
        JobInfo jobInfo = new JobInfo();
        Mockito.doThrow(new FileUploaderException("Exception", HttpStatus.NOT_FOUND)).when(jobInfoService).saveQuartzJob(jobInfo);
        Mockito.doReturn(new ResponseEntity<Object>("Exception", HttpStatus.OK)).when(controller).saveJobInfo(jobInfo);

        ResponseEntity<Object> objectResponse = controller.saveJobInfo(jobInfo);
        assertEquals("Exception", objectResponse.getBody());


    }

    @Test
    void saveJob(){
        RequestJob requestJob = new RequestJob("Outward Test Key", "Outward Job Description", 18);
        Mockito.doNothing().when(schedulerService).saveJob(requestJob);
        assertDoesNotThrow(()-> controller.saveJob(requestJob));
//        RequestJob requestJob = new RequestJob("Outward Test Key", "Outward Job Description", 18);
//        doReturn(new ResponseEntity<Object>("job save successfully.", HttpStatus.OK)).when(controller).saveJob(requestJob);
//
//        ResponseEntity<Object> objectResponse = controller.saveJob(requestJob);
//        assertEquals("job save successfully.", objectResponse.getBody());
    }

    @Test
    void saveJobForException(){
        RequestJob requestJob = new RequestJob("Outward Test Key", "Outward Job Description", 18);
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).saveJob(requestJob);

        ResponseEntity<Object> objectResponse = controller.saveJob(requestJob);
        assertEquals("exception", objectResponse.getBody());
    }

    @Test
    void getJob(){
        JobKey passedKey = new JobKey("Outward Test Pass key");
        JobKey failedKey = new JobKey("Outward Test Fail Key");

        Mockito.when(schedulerService.getJobByKey(passedKey)).thenReturn(null);
        assertDoesNotThrow(()-> controller.getJob(passedKey.getName()));
    }

    @Test
    void getJobForException(){
        JobKey passedKey = new JobKey("Outward Test Pass key");

        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).getJobByKey(passedKey);
        ResponseEntity<Object> objectResponse = controller.getJob(passedKey.getName());
        assertEquals("exception", objectResponse.getBody());
    }

    @Test
    void cancelTrigger(){
        JobKey key = new JobKey("key");
        Mockito.doNothing().when(schedulerService).cancelTrigger(key);
        assertDoesNotThrow(()->{
            controller.cancelTrigger("key");
        });
    }

    @Test
    void cancelTriggerForException(){
        JobKey key = new JobKey("key");
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).cancelTrigger(key);
        ResponseEntity<Object> objectResponse = controller.cancelTrigger(key.getName());
        assertEquals("exception", objectResponse.getBody());

    }

    @Test
    void resumeJob(){
        String jobKey = "Outward Test Job Key";
        Mockito.doNothing().when(schedulerService).resumeJob(new JobKey(jobKey));
        assertDoesNotThrow(()->{
            controller.resumeJob(jobKey);
        });
    }

    @Test
    void resumeJobForException(){
        String jobKey = "Outward Test Job Key";
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).resumeJob(new JobKey(jobKey));
        ResponseEntity<Object> objectResponse = controller.resumeJob(jobKey);
        assertEquals("exception", objectResponse.getBody());

    }


    @Test
    void pauseJob(){
        String jobKey = "Outward Test Job Key";
        Mockito.doNothing().when(schedulerService).pauseJob(new JobKey(jobKey));
        assertDoesNotThrow(()->{
            controller.pauseJob(jobKey);
        });
    }

    @Test
    void pauseJobForException(){
        String jobKey = "Outward Test Job Key";
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).pauseJob(new JobKey(jobKey));
        ResponseEntity<Object> objectResponse = controller.pauseJob(jobKey);
        assertEquals("exception", objectResponse.getBody());
    }

    @Test
    void updateJob(){
        RequestJob job = new RequestJob();
        Mockito.doNothing().when(schedulerService).updateJob(job);
        assertDoesNotThrow(()->{
            controller.updateJob(job);
        });
    }

    @Test
    void updateJobForException(){
        RequestJob job = new RequestJob();
        Mockito.doThrow(new FileUploaderException("exception", HttpStatus.NOT_FOUND)).when(schedulerService).updateJob(job);
        ResponseEntity<Object> objectResponse = controller.updateJob(job);
        assertEquals("exception", objectResponse.getBody());
    }
}
