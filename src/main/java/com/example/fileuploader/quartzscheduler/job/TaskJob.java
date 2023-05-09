package com.example.fileuploader.quartzscheduler.job;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.transferfile.FileTransferService;
import com.example.fileuploader.model.JobInfo;
import com.example.fileuploader.model.OperationType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;


public class TaskJob implements Job {
    private final FileTransferService fileTransferService;

    public TaskJob(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        JobInfo jobInfo = (JobInfo) map.get("jobInfo");

        try{
            if(jobInfo.getOperationType().equals(String.valueOf(OperationType.EXPORT))){
                fileTransferService.setFiles(jobInfo);
            }else{
                fileTransferService.getFiles(jobInfo);
            }
        }catch (FileUploaderException exception){
            LoggerFactory.getLogger("File Uploader").info(exception.getErrorMessage());
        }
    }
}