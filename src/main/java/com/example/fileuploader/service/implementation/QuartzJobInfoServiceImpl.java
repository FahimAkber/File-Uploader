package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.Configuration;
import com.example.fileuploader.model.PathConfiguration;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.response.JobInfoResponse;
import com.example.fileuploader.quartzscheduler.QuartzSchedulerService;
import com.example.fileuploader.service.QuartzJobInfoService;
import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.repository.QuartzJobInfoRepository;
import com.example.fileuploader.service.ServerService;
import org.modelmapper.ModelMapper;
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
            String groupId = UUID.randomUUID().toString();

            QuartzJobInfo quartzJobInfo = null;

            for (PathConfiguration path : jobInfo.getPaths()){
                quartzJobInfo = new QuartzJobInfo();
                String jobKey = UUID.randomUUID().toString();

                quartzJobInfo.setJobKey(jobKey);
                quartzJobInfo.setJobGroup(groupId);
                quartzJobInfo.setFileExtension(path.getFileExtension());
                quartzJobInfo.setSourceServer(serverService.findById(jobInfo.getSourceServerId()));
                quartzJobInfo.setSourcePath(path.getSourcePath());
                quartzJobInfo.setDestinationServer(serverService.findById(path.getDestinationServerId()));
                quartzJobInfo.setDestinationPath(path.getDestinationPath());
                quartzJobInfo.setCreatedBy(System.getProperty("user.home"));
                quartzJobInfo.setExecutedAt(Calendar.getInstance().getTime());
                quartzSchedulerService.saveJob(quartzJobInfo);
                quartzJobInfoRepository.save(quartzJobInfo);
            }

            //TODO: NEED CHECK IF THE JOB GROUP ALREADY EXIST

            return new JobInfoResponse(groupId);

        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<QuartzJobInfo> findJobInfoByGroupId(String groupId) {
        try{
            return quartzJobInfoRepository.findByJobGroup(groupId);
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
    public Map<String, List<QuartzJobInfo>> getQuartzJobInfos() {
        List<QuartzJobInfo> jobInfos = quartzJobInfoRepository.findAll();
        return convertedList(jobInfos);
    }

    private Map<String, List<QuartzJobInfo>> convertedList(List<QuartzJobInfo> jobInfos){
        return jobInfos.stream().collect(Collectors.groupingBy(QuartzJobInfo::getJobGroup));
    }
}
