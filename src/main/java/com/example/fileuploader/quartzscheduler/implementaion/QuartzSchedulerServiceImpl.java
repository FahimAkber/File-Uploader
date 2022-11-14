package com.example.fileuploader.quartzscheduler.implementaion;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.quartzscheduler.job.TaskJob;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.RequestJob;
import com.example.fileuploader.model.RequestTrigger;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
public class QuartzSchedulerServiceImpl implements QuartzSchedulerService {

    private final Scheduler scheduler;
    private final QuartzJobInfoService quartzJobInfoService;
    private static final String SCHEDULER_NAME = "Scheduler";

    public QuartzSchedulerServiceImpl(Scheduler scheduler, QuartzJobInfoService quartzJobInfoService){
        this.scheduler = scheduler;
        this.quartzJobInfoService = quartzJobInfoService;
    }

    @Override
    public void saveJob(RequestJob requestJob) {
        if(requestJob.getJobKey() == null || requestJob.getJobKey().isEmpty()){
            throw new FileUploaderException("Job key can't be null", HttpStatus.NOT_FOUND);
        }
        if(requestJob.getJobDescription() == null || requestJob.getJobDescription().isEmpty()){
            throw new FileUploaderException("Job Description can't be null", HttpStatus.NOT_FOUND);
        }
        JobInfo quartzJobInfoById = quartzJobInfoService.getQuartzJobInfoById(requestJob.getJobInformationId());
        if(quartzJobInfoById == null){
            throw new FileUploaderException("No job information available with the job id: "+ requestJob.getJobInformationId(), HttpStatus.NOT_FOUND);
        }

        JobDetail job = buildJobDetail(requestJob, quartzJobInfoById);

        try {
            if(scheduler.checkExists(job.getKey())){
                LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Exist with the key: {}", job.getKey().getName());
            }else{
                scheduler.addJob(job, true);
                LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Created for the key: {}", job.getKey().getName());
            }
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void saveTrigger(RequestTrigger requestTrigger) {
        if(requestTrigger.getJob() == null){
            throw new FileUploaderException("Must to provide specific job", HttpStatus.NOT_FOUND);
        }
        SimpleTrigger simpleTrigger = buildJobTrigger(requestTrigger);

        try {
            scheduler.scheduleJob(simpleTrigger);
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JobDetail buildJobDetail(RequestJob requestJob, JobInfo quartzJobInfo) {
        JobDataMap map = new JobDataMap();
        map.put("jobInfo", quartzJobInfo);

        return JobBuilder.newJob(TaskJob.class)
                .withIdentity(requestJob.getJobKey())
                .withDescription(requestJob.getJobDescription())
                .usingJobData(map)
                .storeDurably()
                .build();
    }

    private SimpleTrigger buildJobTrigger(RequestTrigger requestTrigger) {
        TriggerBuilder<Trigger> buildTrigger = TriggerBuilder.newTrigger();
        buildTrigger.forJob(requestTrigger.getJob());
        buildTrigger.withIdentity("trigger key: "+requestTrigger.getJob().getKey());
        buildTrigger.withDescription("trigger description: "+requestTrigger.getJob().getDescription());

        if(requestTrigger.getStartAt() == null){
            buildTrigger.startNow();
        }else{
            buildTrigger.startAt(requestTrigger.getStartAt());
        }

        SimpleScheduleBuilder scheduleBuilder = simpleSchedule();

        if(requestTrigger.getTotalInterval() == 0){
            scheduleBuilder.repeatForever();
        }else{
            scheduleBuilder.withRepeatCount(requestTrigger.getTotalInterval());
        }

        scheduleBuilder.withIntervalInSeconds(requestTrigger.getFrequency());
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
    public void updateJob(RequestJob requestJob) {
        JobInfo info = quartzJobInfoService.getQuartzJobInfoById(requestJob.getJobInformationId());
        JobDetail jobDetail = buildJobDetail(requestJob, info);
        try {
            if(!scheduler.checkExists(jobDetail.getKey())){
                LoggerFactory.getLogger(SCHEDULER_NAME).info("No job found by this key: {}", jobDetail.getKey().getName());
                return;
            }
            scheduler.addJob(jobDetail, true);
            LoggerFactory.getLogger(SCHEDULER_NAME).info("Job Updated for key: {}", jobDetail.getKey().getName());
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
