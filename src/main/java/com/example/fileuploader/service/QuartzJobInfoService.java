package com.example.fileuploader.service;

import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;
import com.example.fileuploader.model.response.MessageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface QuartzJobInfoService {
    JobInfoResponse saveQuartzJob(JobInfo quartzJobInfo);
    QuartzJobInfo findJobInfoByJobKey(String jobKey);

//    Not working: need to pass quartzJobInfo
    JobInfo getQuartzJobInfoById(int id);
    Page<QuartzJobInfo> getQuartzJobInfos(Integer pageNo, Integer pageSize);
    MessageResponse deleteJobInfo(String jobKey);
    Page<QuartzJobInfo> getJobInfoByServer(String server, Integer page, Integer size);
}
