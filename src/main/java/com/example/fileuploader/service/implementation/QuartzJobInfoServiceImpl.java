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
import com.example.fileuploader.util.Util;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class QuartzJobInfoServiceImpl implements QuartzJobInfoService {
    private final QuartzJobInfoRepository quartzJobInfoRepository;
    private final ModelMapper modelMapper;
    private final Configuration configuration;
    private final QuartzSchedulerService quartzSchedulerService;

    public QuartzJobInfoServiceImpl(QuartzJobInfoRepository quartzJobInfoRepository, ModelMapper modelMapper, Configuration configuration, QuartzSchedulerService quartzSchedulerService){
        this.quartzJobInfoRepository = quartzJobInfoRepository;
        this.modelMapper = modelMapper;
        this.configuration = configuration;
        this.quartzSchedulerService = quartzSchedulerService;
    }

    @Override
    public JobInfoResponse saveQuartzJob(JobInfo jobInfo) {
        try{
            Util.checkRequiredField("operationType", jobInfo.getOperationType());
            Util.checkRequiredField("fileExtension", jobInfo.getFileExtension());
            Util.checkRequiredField("remoteHost", jobInfo.getRemoteHost());
            Util.checkRequiredField("remoteUser", jobInfo.getRemoteUser());
            Util.checkRequiredFile("multipartFile", jobInfo.getMultipartFile());

            String fileName = uploadFileToLocal(jobInfo.getMultipartFile());
            String groupId = generateGroupId(jobInfo.getOperationType(), jobInfo.getRemoteHost());
            QuartzJobInfo quartzJobInfo = null;

            for (PathConfiguration path : jobInfo.getPaths()){
                quartzJobInfo = new QuartzJobInfo();
                BeanUtils.copyProperties(jobInfo, quartzJobInfo);
                quartzJobInfo.setLocalPath(path.getLocalPath());
                quartzJobInfo.setRemotePath(path.getRemotePath());
                quartzJobInfo.setFileName(fileName);
                quartzJobInfo.setCreatedBy(System.getProperty("user.home"));
                quartzJobInfo.setExecutedAt(Calendar.getInstance().getTime());
                quartzJobInfo.setJobKey(quartzSchedulerService.saveJob(quartzJobInfo));
                quartzJobInfo.setJobGroup(groupId);
                quartzJobInfoRepository.save(quartzJobInfo);
            }


            //TODO: NEED CHECK IF THE JOB GROUP ALREADY EXIST

            return new JobInfoResponse(groupId);

        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private String generateGroupId(String operationType, String remoteHost){
        return new StringBuilder(operationType).append(" from ").append(remoteHost).toString();
    }

    private String generateFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + extension;
    }

    private String uploadFileToLocal(MultipartFile multipartFile) throws Exception {

        String fileName = generateFileName(multipartFile.getOriginalFilename());;
        Path path = Paths.get(configuration.getFileStoreLocation(), fileName);

        if(!Files.exists(path.getParent())){
            Files.createDirectory(path.getParent());
        }

        multipartFile.transferTo(path);
        return fileName;
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
