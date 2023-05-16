package com.example.fileuploader.transferfile;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.example.fileuploader.model.JobInfo;

public interface FileTransferService {
    Session createSession(String remoteUser, String remotePassword, String remoteHost, int remotePort);
    ChannelSftp createChannelSftp(Session session);
    void getFiles(JobInfo jobInfo);
    void getTestFiles();
    void setFiles(JobInfo jobInfo);
    void destroyConnection(Session session, ChannelSftp channelSftp);
}