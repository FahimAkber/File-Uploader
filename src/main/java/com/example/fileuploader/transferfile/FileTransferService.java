package com.example.fileuploader.transferfile;


import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.example.fileuploader.model.JobInfo;

public interface FileTransferService {
    void getFiles(QuartzJobInfo jobInfo);
    void setFiles();
}