package com.example.fileuploader.quartzscheduler;

import com.example.fileuploader.model.RequestJob;
import com.example.fileuploader.model.RequestTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;


public interface QuartzSchedulerService {
    void saveJob(RequestJob requestJob);
    void saveTrigger(RequestTrigger requestTrigger);
    JobDetail getJobByKey(JobKey jobKey);
    void updateJob(RequestJob requestJob);
    void cancelTrigger(JobKey jobKey);
    void pauseJob(JobKey key);
    void resumeJob(JobKey key);
}
