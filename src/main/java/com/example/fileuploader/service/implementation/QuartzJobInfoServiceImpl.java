package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.repository.QuartzJobInfoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class QuartzJobInfoServiceImpl implements QuartzJobInfoService {
    private final QuartzJobInfoRepository quartzJobInfoRepository;
    private final ModelMapper modelMapper;

    public QuartzJobInfoServiceImpl(QuartzJobInfoRepository quartzJobInfoRepository, ModelMapper modelMapper){
        this.quartzJobInfoRepository = quartzJobInfoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveQuartzJob(JobInfo jobInfo) {
        if(jobInfo.getJobType() == null || jobInfo.getJobType().isEmpty()){
            throw new FileUploaderException("Job type cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getOperationType() == null || jobInfo.getOperationType().isEmpty()){
            throw new FileUploaderException("Operation type cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getLocalExtension() == null || jobInfo.getLocalExtension().isEmpty()){
            throw new FileUploaderException("Local extension cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getRemoteExtension() == null || jobInfo.getRemoteExtension().isEmpty()){
            throw new FileUploaderException("Remote extension cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getProjectName() == null || jobInfo.getProjectName().isEmpty()){
            throw new FileUploaderException("Project name cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getRemoteHost() == null || jobInfo.getRemoteHost().isEmpty()){
            throw new FileUploaderException("Remote host cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getRemoteUser() == null || jobInfo.getRemoteUser().isEmpty()){
            throw new FileUploaderException("Remote user cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getRemotePassword() == null || jobInfo.getRemotePassword().isEmpty()){
            throw new FileUploaderException("Remote password cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getRemotePath() == null || jobInfo.getRemotePath().isEmpty()){
            throw new FileUploaderException("Remote path cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        if(jobInfo.getLocalPath() == null || jobInfo.getLocalPath().isEmpty()){
            throw new FileUploaderException("Local path cannot be null or empty String", HttpStatus.NOT_FOUND);
        }
        QuartzJobInfo quartzJobInfo = new QuartzJobInfo(jobInfo.getJobType(), jobInfo.getOperationType(), jobInfo.getLocalExtension(),
                                        jobInfo.getRemoteExtension(), jobInfo.getProjectName(), jobInfo.getRemoteHost(), jobInfo.getRemotePort(),
                                        jobInfo.getRemoteUser(), jobInfo.getRemotePassword(), jobInfo.getRemotePath(), jobInfo.getLocalPath());
        quartzJobInfo.setCreatedBy(System.getProperty("user.home"));
        quartzJobInfo.setExecutedAt(Calendar.getInstance().getTime());
        quartzJobInfoRepository.save(quartzJobInfo);
    }

    @Override
    public JobInfo getQuartzJobInfoById(int id) {
        try{
            return modelMapper.map(quartzJobInfoRepository.findById(id).get(), JobInfo.class);
        }catch (Exception exception){
            throw new FileUploaderException("No job found by this id: "+id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<JobInfo> getQuartzJobInfos() {
        List<QuartzJobInfo> jobInfos = quartzJobInfoRepository.findAll();
        return convertedList(jobInfos);
    }

    private List<JobInfo> convertedList(List<QuartzJobInfo> jobInfos){
        List<JobInfo> jobInfoList = new ArrayList<>();
        for (QuartzJobInfo jobInfo : jobInfos) {
            jobInfoList.add(modelMapper.map(jobInfo, JobInfo.class));
        }
        return jobInfoList;
    }
}
