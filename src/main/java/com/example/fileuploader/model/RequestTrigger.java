package com.example.fileuploader.model;

import org.quartz.JobDetail;
import java.util.Date;

public class RequestTrigger {
    private JobDetail job;
    private int totalInterval;
    private int frequency;
    private Date startAt;

    public RequestTrigger(){}

    public RequestTrigger(int totalInterval, int frequency, Date startAt) {
        this.totalInterval = totalInterval;
        this.frequency = frequency;
        this.startAt = startAt;
    }

    public RequestTrigger(JobDetail job, int totalInterval, int frequency, Date startAt) {
        this.job = job;
        this.totalInterval = totalInterval;
        this.frequency = frequency;
        this.startAt = startAt;
    }

    public JobDetail getJob() {
        return job;
    }

    public void setJob(JobDetail job) {
        this.job = job;
    }

    public int getTotalInterval() {
        return totalInterval;
    }

    public void setTotalInterval(int totalInterval) {
        this.totalInterval = totalInterval;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }
}
