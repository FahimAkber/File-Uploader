package com.example.fileuploader.controller;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.JobRequest;
import com.example.fileuploader.model.SchedulerRequest;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.transferfile.FileTransferService;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@RestController
public class FileTransferController {

    private final QuartzJobInfoService quartzJobInfoService;
    private final QuartzSchedulerService quartzSchedulerService;
    private final FileTransferService fileTransferService;

    public FileTransferController(QuartzJobInfoService quartzJobInfoService, QuartzSchedulerService quartzSchedulerService, FileTransferService fileTransferService) {
        this.quartzJobInfoService = quartzJobInfoService;
        this.quartzSchedulerService = quartzSchedulerService;
        this.fileTransferService = fileTransferService;
    }

    @PostMapping("schedule-file")
    public ResponseEntity<Object> schedulingFile(@RequestBody SchedulerRequest schedulerRequest){
        List<QuartzJobInfo> jobs = quartzJobInfoService.findJobInfoByGroupId(schedulerRequest.getJobGroupId());
        int totalInterval = schedulerRequest.getTotalInterval();
        int frequency = schedulerRequest.getFrequency();
        Date startAt = schedulerRequest.getStartAt();

        for(QuartzJobInfo job : jobs){
            JobKey key = new JobKey(job.getJobKey());
            JobDetail jobDetail = quartzSchedulerService.getJobByKey(key);
            quartzSchedulerService.saveTrigger(jobDetail, totalInterval, frequency, startAt);
        }


        return ResponseEntity.ok("Successfully schedule the job");
    }

    @PostMapping(value = "save-job-info", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> saveJobInfo(@RequestPart("jobInfo") JobInfo jobInfo, @RequestPart("sourceMultipartFile") MultipartFile sourceMultipartFile, @RequestPart("destinationMultipartFile") MultipartFile destinationMultipartFile){
        jobInfo.setSourceMultipartFile(sourceMultipartFile);
        jobInfo.setDestinationMultipartFile(destinationMultipartFile);
        return ResponseEntity.ok(quartzJobInfoService.saveQuartzJob(jobInfo));
    }

    @GetMapping(value = "get-job-info")
    public ResponseEntity<Object> getJobInfo(){
        return ResponseEntity.ok(quartzJobInfoService.getQuartzJobInfos());
    }

    @GetMapping("get-job/{jobKey}")
    public ResponseEntity<Object> getJob(@PathVariable String jobKey){
        try{
            JobKey key = new JobKey(jobKey);
            JobDetail jobByKey = quartzSchedulerService.getJobByKey(key);

            return ResponseEntity.ok(jobByKey);
        }catch (FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("cancel-running-job/{jobKey}")
    public ResponseEntity<Object> cancelTrigger(@PathVariable String jobKey){
        try{
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.cancelTrigger(key);

            return ResponseEntity.ok("Cancel running jobs by key: "+key.getName());
        }catch(FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("resume-running-job/{jobKey}")
    public ResponseEntity<Object> resumeJob(@PathVariable String jobKey){
        try{
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.resumeJob(key);

            return ResponseEntity.ok("Resume jobs by key: "+key.getName());
        }catch(FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @GetMapping("pause-job/{jobKey}")
    public ResponseEntity<Object> pauseJob(@PathVariable String jobKey){
        try{
            JobKey key = new JobKey(jobKey);
            quartzSchedulerService.pauseJob(key);

            return ResponseEntity.ok("Pause job by key: "+key.getName());
        }catch(FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("update-job")
    public ResponseEntity<Object> updateJob(@RequestBody JobRequest jobRequest){
        try{
            quartzSchedulerService.updateJob(jobRequest);
            return ResponseEntity.ok("Job updated successfully.");
        }catch (FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("schedule-private-job")
    public ResponseEntity<Object> schedulingPrivateJob(@RequestBody SchedulerRequest schedulerRequest){
        int totalInterval = schedulerRequest.getTotalInterval();
        int frequency = schedulerRequest.getFrequency();
        Date startAt = schedulerRequest.getStartAt();
        String jobType = schedulerRequest.getJobType();

        quartzSchedulerService.savePrivateJob(jobType, totalInterval, frequency, startAt);

        return ResponseEntity.ok("Successfully schedule the job");
    }
}

