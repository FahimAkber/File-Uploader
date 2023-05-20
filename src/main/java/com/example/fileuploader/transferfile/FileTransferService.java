package com.example.fileuploader.transferfile;


import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.example.fileuploader.model.JobInfo;

public interface FileTransferService {
    Session createSession(String remoteUser, String remoteHost, int remotePort, String fileName);
    ChannelSftp createChannelSftp(Session session);
    void getFiles(QuartzJobInfo jobInfo);
    void getTestFiles();
    void setFiles(QuartzJobInfo jobInfo);
    void destroyConnection(Session session, ChannelSftp channelSftp);
}