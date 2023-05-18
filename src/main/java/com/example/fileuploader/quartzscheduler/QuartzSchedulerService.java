package com.example.fileuploader.quartzscheduler;

import com.example.fileuploader.model.JobRequest;
import com.example.fileuploader.model.RequestTrigger;
import com.example.fileuploader.model.SchedulerRequest;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import org.quartz.JobDetail;
import org.quartz.JobKey;


public interface QuartzSchedulerService {
    String saveJob(QuartzJobInfo jobInfo) throws Exception;
    void saveTrigger(JobDetail jobDetail, SchedulerRequest schedulerRequest);
    JobDetail getJobByKey(JobKey jobKey);
    void updateJob(JobRequest jobRequest);
    void cancelTrigger(JobKey jobKey);
    void pauseJob(JobKey key);
    void resumeJob(JobKey key);
}
