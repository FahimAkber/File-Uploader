package com.example.fileuploader.service;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;

import java.util.List;

public interface QuartzJobInfoService {
    JobInfoResponse saveQuartzJob(JobInfo quartzJobInfo);
    JobInfo getQuartzJobInfoById(int id);
    List<JobInfo> getQuartzJobInfos();
}
