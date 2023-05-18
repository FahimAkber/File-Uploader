package com.example.fileuploader.quartzscheduler.implementaion;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.*;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.quartzscheduler.job.TaskJob;
import com.example.fileuploader.service.QuartzJobInfoService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
public class QuartzSchedulerServiceImpl implements QuartzSchedulerService {

    private final Scheduler scheduler;
    private static final String SCHEDULER_NAME = "Scheduler";

    public QuartzSchedulerServiceImpl(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public String saveJob(QuartzJobInfo jobInfo) throws Exception{
        try {
            JobDetail job = buildJobDetail(jobInfo);
            if(scheduler.checkExists(job.getKey())){
                LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Exist with the key: {}", job.getKey().getName());
                throw new Exception("Job Exist with the key: " + job.getKey().getName());
            }else{
                scheduler.addJob(job, true);
                LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Created for the key: {}", job.getKey().getName());
            }
            return job.getKey().getName();
        } catch (SchedulerException e) {
            throw new Exception(e);
        }
    }

    private JobDetail buildJobDetail(QuartzJobInfo jobInfo) {
        JobDataMap map = new JobDataMap();
        map.put("jobInfo", jobInfo);

        return JobBuilder.newJob(TaskJob.class)
                .withIdentity(buildIdentity(jobInfo.getRemoteHost(), jobInfo.getOperationType(), jobInfo.getRemotePath(), jobInfo.getLocalPath()))
                .withDescription(buildDescription(jobInfo.getOperationType()))
                .usingJobData(map)
                .storeDurably()
                .build();
    }

    private String buildDescription(String operationType) {
        return operationType.equals(OperationType.IMPORT.toString()) ? "Import file from remote server" : "Export file to remote server";
    }

    private String buildIdentity(String remoteHost, String operationType, String remotePath, String localPath) {
        StringBuilder stringBuilder = new StringBuilder(operationType).append(" from ").append(remoteHost);
        if(operationType.equals(OperationType.IMPORT.toString())){
            stringBuilder.append(" : ").append(remotePath).append(" to ").append(localPath);
        }else{
            stringBuilder.append(" : ").append(localPath).append(" to ").append(remotePath);
        }

        return stringBuilder.toString();
    }

    @Override
    public void saveTrigger(JobDetail jobDetail, SchedulerRequest schedulerRequest) {
        if(jobDetail == null){
            throw new FileUploaderException("Must to provide specific job", HttpStatus.NOT_FOUND);
        }
        SimpleTrigger simpleTrigger = buildJobTrigger(schedulerRequest, jobDetail);

        try {
            scheduler.scheduleJob(simpleTrigger);
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private SimpleTrigger buildJobTrigger(SchedulerRequest schedulerRequest, JobDetail jobDetail) {
        TriggerBuilder<Trigger> buildTrigger = TriggerBuilder.newTrigger();
        buildTrigger.forJob(jobDetail);
        buildTrigger.withIdentity("trigger key: "+ jobDetail.getKey());
        buildTrigger.withDescription("trigger description: "+ jobDetail.getDescription());

        if(schedulerRequest.getStartAt() == null){
            buildTrigger.startNow();
        }else{
            buildTrigger.startAt(schedulerRequest.getStartAt());
        }

        SimpleScheduleBuilder scheduleBuilder = simpleSchedule();

        if(schedulerRequest.getTotalInterval() == 0){
            scheduleBuilder.repeatForever();
        }else{
            scheduleBuilder.withRepeatCount(schedulerRequest.getTotalInterval());
        }

        scheduleBuilder.withIntervalInSeconds(schedulerRequest.getFrequency());
        buildTrigger.withSchedule(scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires());

        return (SimpleTrigger) buildTrigger.build();
    }

    @Override
    public JobDetail getJobByKey(JobKey jobKey) {
        JobDetail jobDetail = null;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return jobDetail;
    }

    @Override
    public void updateJob(JobRequest jobRequest) {
//        JobInfo info = quartzJobInfoService.getQuartzJobInfoById(jobRequest.getJobInformationId());
//        JobDetail jobDetail = buildJobDetail(jobRequest, info);
//        try {
//            if(!scheduler.checkExists(jobDetail.getKey())){
//                LoggerFactory.getLogger(SCHEDULER_NAME).info("No job found by this key: {}", jobDetail.getKey().getName());
//                return;
//            }
//            scheduler.addJob(jobDetail, true);
//            LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Updated for key: {}", jobDetail.getKey().getName());
//        } catch (SchedulerException e) {
//            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @Override
    public void cancelTrigger(JobKey jobKey) {
        try {
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            for(Trigger trigger : triggersOfJob){
                scheduler.unscheduleJob(trigger.getKey());
                LoggerFactory.getLogger(SCHEDULER_NAME).info("Cancel Running Job");
            }
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void pauseJob(JobKey key) {
        try {
            scheduler.pauseJob(key);
            LoggerFactory.getLogger(SCHEDULER_NAME).info("Pause Running Job for key: {}", key.getName());
        } catch (SchedulerException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void resumeJob(JobKey key) {
        try {
            boolean isEmpty = true;
            JobDetail job = scheduler.getJobDetail(key);
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
            for(Trigger t : triggersOfJob){
                if(scheduler.getTriggerState(t.getKey()) == Trigger.TriggerState.PAUSED){
                    scheduler.resumeJob(job.getKey());
                    LoggerFactory.getLogger(SCHEDULER_NAME).info("Job paused for key: {}", job.getKey().getName());
                    isEmpty = false;
                }
            }

            if(isEmpty){
                LoggerFactory.getLogger(SCHEDULER_NAME).info("No paused job for key: {}", job.getKey().getName());
            }
        } catch (SchedulerException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
