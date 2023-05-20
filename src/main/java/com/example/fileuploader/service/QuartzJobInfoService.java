package com.example.fileuploader.service;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;

import java.util.List;
import java.util.Map;

public interface QuartzJobInfoService {
    JobInfoResponse saveQuartzJob(JobInfo quartzJobInfo);
    List<QuartzJobInfo> findJobInfoByGroupId(String groupId);

//    Not working: need to pass quartzJobInfo
    JobInfo getQuartzJobInfoById(int id);
    Map<String, List<QuartzJobInfo>> getQuartzJobInfos();

}
