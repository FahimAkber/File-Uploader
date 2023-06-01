package com.example.fileuploader.service;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;

import java.util.List;
import java.util.Map;

public interface QuartzJobInfoService {
    JobInfoResponse saveQuartzJob(JobInfo quartzJobInfo);
    QuartzJobInfo findJobInfoByJobKey(String jobKey);

//    Not working: need to pass quartzJobInfo
    JobInfo getQuartzJobInfoById(int id);
    List<QuartzJobInfo> getQuartzJobInfos();

}
