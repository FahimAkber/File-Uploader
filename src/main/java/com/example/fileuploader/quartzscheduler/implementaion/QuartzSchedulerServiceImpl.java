package com.example.fileuploader.quartzscheduler.implementaion;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.*;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.quartzscheduler.job.TaskJob;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
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
                .withIdentity(jobInfo.getJobKey())
                .withDescription(buildDescription(jobInfo.getSourceHost(), jobInfo.getDestinationPath(), jobInfo.getDestinationHost(), jobInfo.getDestinationPath()))
                .usingJobData(map)
                .storeDurably()
                .build();
    }

    private String buildDescription(String sourceHost, String sourcePath, String destinationHost, String destinationPath) {
        return new StringBuilder("Fetch files from ").append(sourceHost).append(" : ").append(sourcePath).append(" and send to ").append(destinationHost).append(" : ").append(destinationPath).toString();
    }

    @Override
    public void saveTrigger(JobDetail jobDetail, int totalInterval, int frequency, Date startAt) {
        if(jobDetail == null){
            throw new FileUploaderException("Must to provide specific job", HttpStatus.NOT_FOUND);
        }
        try {
            if(!scheduler.checkExists(jobDetail.getKey())){
                scheduler.addJob(jobDetail, true);
            }
            SimpleTrigger simpleTrigger = buildJobTrigger(jobDetail, totalInterval, frequency, startAt);
            if(scheduler.checkExists(simpleTrigger.getKey())){
                throw new FileUploaderException("Job already running", HttpStatus.BAD_REQUEST);
            }else{
                scheduler.scheduleJob(simpleTrigger);
            }
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FileUploaderException exception){
            throw exception;
        }
    }



    private SimpleTrigger buildJobTrigger(JobDetail jobDetail, int totalInterval, int frequency, Date startAt) {
        TriggerBuilder<Trigger> buildTrigger = TriggerBuilder.newTrigger();
        buildTrigger.forJob(jobDetail);
        buildTrigger.withIdentity("trigger key: "+ jobDetail.getKey());
        buildTrigger.withDescription("trigger description: "+ jobDetail.getDescription());

        if(startAt == null){
            buildTrigger.startNow();
        }else{
            buildTrigger.startAt(startAt);
        }

        SimpleScheduleBuilder scheduleBuilder = simpleSchedule();

        if(totalInterval == 0){
            scheduleBuilder.repeatForever();
        }else{
            scheduleBuilder.withRepeatCount(totalInterval);
        }

        scheduleBuilder.withIntervalInSeconds(frequency);
        buildTrigger.withSchedule(scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires());

        return (SimpleTrigger) buildTrigger.build();
    }

    @Override
    public JobDetail getJobByKey(JobKey jobKey) {
        JobDetail jobDetail = null;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
            return jobDetail;
        } catch (SchedulerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @Override
    public void savePrivateJob(String jobType, int totalInterval, int frequency, Date startAt) {
        JobDataMap map = new JobDataMap();
        map.put("jobType", jobType);

        JobDetail jobDetail = JobBuilder.newJob(TaskJob.class)
                .withIdentity("Private Job Type : ".concat(jobType))
                .withDescription(jobType.equals("Sender") ? "Job for sending file to destination" : "Job for processing files")
                .usingJobData(map)
                .storeDurably()
                .build();

        saveTrigger(jobDetail, totalInterval, frequency, startAt);
    }
}
