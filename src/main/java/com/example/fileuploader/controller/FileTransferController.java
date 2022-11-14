package com.example.fileuploader.controller;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.RequestJob;
import com.example.fileuploader.model.SchedulerJobDTO;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.mislbd.fileuploader.model.*;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileTransferController {

    private final QuartzJobInfoService quartzJobInfoService;
    private final QuartzSchedulerService quartzSchedulerService;

    public FileTransferController(QuartzJobInfoService quartzJobInfoService, QuartzSchedulerService quartzSchedulerService) {
        this.quartzJobInfoService = quartzJobInfoService;
        this.quartzSchedulerService = quartzSchedulerService;
    }

    @PostMapping("schedule-file")
    public ResponseEntity<Object> schedulingFile(@RequestBody SchedulerJobDTO job){
        try{
            JobKey key = new JobKey(job.getJobKey());
            JobDetail jobDetail = quartzSchedulerService.getJobByKey(key);
            job.getRequestTrigger().setJob(jobDetail);
            quartzSchedulerService.saveTrigger(job.getRequestTrigger());

            return ResponseEntity.ok("Successfully schedule the job");
        }catch (FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("save-job-info")
    public ResponseEntity<Object> saveJobInfo(@RequestBody JobInfo jobInfo){
        try{
            quartzJobInfoService.saveQuartzJob(jobInfo);
            return ResponseEntity.ok("Job info save successfully.");
        }catch (FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }

    @PostMapping("save-job")
    public ResponseEntity<Object> saveJob(@RequestBody RequestJob requestJob){
        try{
            quartzSchedulerService.saveJob(requestJob);
            return ResponseEntity.ok("Job save successfully");
        }catch(FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
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
    public ResponseEntity<Object> updateJob(@RequestBody RequestJob requestJob){
        try{
            quartzSchedulerService.updateJob(requestJob);
            return ResponseEntity.ok("Job updated successfully.");
        }catch (FileUploaderException exception){
            return new ResponseEntity<>(exception.getErrorMessage(), exception.getErrorCode());
        }
    }
}

