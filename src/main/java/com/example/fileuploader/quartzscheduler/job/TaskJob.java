package com.example.fileuploader.quartzscheduler.job;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.transferfile.FileTransferService;
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
        try{
            JobDataMap map = jobExecutionContext.getMergedJobDataMap();
            QuartzJobInfo jobInfo = (QuartzJobInfo) map.get("jobInfo");

            if(jobInfo != null){
                fileTransferService.getFiles(jobInfo);
            }else{
                String jobType = (String) map.get("jobType");
                if(jobType.equals("Sender")){
                    fileTransferService.setFiles();
                }else if(jobType.equals("Processor")){
                    //Call processor method
                }else{
                    //do something
                }
            }

        }catch (FileUploaderException exception){
            LoggerFactory.getLogger("File Uploader").info(exception.getErrorMessage());
        }catch (Exception exception){
            LoggerFactory.getLogger("File Uploader").info(exception.getMessage());
        }
    }
}