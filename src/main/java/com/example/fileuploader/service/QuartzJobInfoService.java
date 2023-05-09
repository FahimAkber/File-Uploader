package com.example.fileuploader.service;

import com.example.fileuploader.model.JobInfo;

import java.util.List;

public interface QuartzJobInfoService {
    void saveQuartzJob(JobInfo quartzJobInfo);
    JobInfo getQuartzJobInfoById(int id);
    List<JobInfo> getQuartzJobInfos();
}
