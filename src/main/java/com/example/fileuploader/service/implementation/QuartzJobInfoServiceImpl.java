package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.Configuration;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;
import com.example.fileuploader.model.response.MessageResponse;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.repository.QuartzJobInfoRepository;
import com.example.fileuploader.service.ServerService;
import com.example.fileuploader.util.Util;
import org.modelmapper.ModelMapper;
import org.quartz.JobKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuartzJobInfoServiceImpl implements QuartzJobInfoService {
    private final QuartzJobInfoRepository quartzJobInfoRepository;
    private final ModelMapper modelMapper;
    private final Configuration configuration;
    private final ServerService serverService;
    private final QuartzSchedulerService quartzSchedulerService;

    public QuartzJobInfoServiceImpl(QuartzJobInfoRepository quartzJobInfoRepository, ModelMapper modelMapper, Configuration configuration, ServerService serverService, QuartzSchedulerService quartzSchedulerService){
        this.quartzJobInfoRepository = quartzJobInfoRepository;
        this.modelMapper = modelMapper;
        this.configuration = configuration;
        this.serverService = serverService;
        this.quartzSchedulerService = quartzSchedulerService;
    }

    private String buildIdentity(String sourceHost, String destinationHost, String sourcePath, String destinationPath) {
        return new StringBuilder("Fetch files from ").append(sourceHost).append(" : ").append(sourcePath).append(" and send to ").append(destinationHost).append(" : ").append(destinationPath).toString();
    }


    @Override
    public JobInfoResponse saveQuartzJob(JobInfo jobInfo) {
        try{
            QuartzJobInfo quartzJobInfo = null;
            quartzJobInfo = quartzJobInfoRepository.findBySourceAndDestination(jobInfo.getSourceServerId(), jobInfo.getSourcePath(), jobInfo.getDestinationServerId(), jobInfo.getDestinationPath());
            if(quartzJobInfo != null){
                throw new Exception("Already configured this configuration: Source: "
                        + jobInfo.getSourceServerId() + "/"
                        + jobInfo.getSourcePath() + " to Destination: "
                        + jobInfo.getDestinationServerId() + "/"
                        + jobInfo.getDestinationPath());
            }

            quartzJobInfo = new QuartzJobInfo();
            String jobKey = UUID.randomUUID().toString();

            quartzJobInfo.setJobKey(jobKey);
            quartzJobInfo.setFileExtension(jobInfo.getFileExtension());
            quartzJobInfo.setSourceServer(serverService.findById(jobInfo.getSourceServerId()));
            quartzJobInfo.setSourcePath(jobInfo.getSourcePath());
            quartzJobInfo.setDestinationServer(serverService.findById(jobInfo.getDestinationServerId()));
            quartzJobInfo.setDestinationPath(jobInfo.getDestinationPath());
            quartzJobInfo.setCreatedBy(System.getProperty("user.home"));
            quartzJobInfo.setExecutedAt(Calendar.getInstance().getTime());
            quartzSchedulerService.saveJob(quartzJobInfo);
            quartzJobInfoRepository.save(quartzJobInfo);

            return new JobInfoResponse(jobKey);

        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public QuartzJobInfo findJobInfoByJobKey(String jobKey) {
        try{
            return quartzJobInfoRepository.findByJobKey(jobKey);
        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String generateGroupId(String remoteHost){
        return new StringBuilder("Connection").append(" : ").append(remoteHost).toString();
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
    public List<QuartzJobInfo> getQuartzJobInfos(Integer pageNo, Integer pageSize) {
        return quartzJobInfoRepository
                .findAll(Util.getPageableObject(pageNo, pageSize))
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse deleteJobInfo(String jobKey) {
        try{
            QuartzJobInfo jobInfo = findJobInfoByJobKey(jobKey);
            quartzSchedulerService.deleteJob(new JobKey(jobKey));
            quartzJobInfoRepository.delete(jobInfo);

            return new MessageResponse(jobKey.concat(" deleted."));
        }catch(Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
