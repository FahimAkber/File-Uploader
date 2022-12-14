package com.example.fileuploader.model;

import java.io.Serializable;

public class JobInfo implements Serializable {
    private int id;
    private String jobType;
    private String operationType;
    private String localExtension;
    private String remoteExtension;
    private String projectName;
    private String remoteHost;
    private int remotePort;
    private String remoteUser;
    private String remotePassword;
    private String remotePath;
    private String localPath;


    public JobInfo() {
    }

    public JobInfo(String jobType, String operationType, String localExtension, String remoteExtension, String projectName, String remoteHost, int remotePort, String remoteUser, String remotePassword, String remotePath, String localPath) {
        this.jobType = jobType;
        this.operationType = operationType;
        this.localExtension = localExtension;
        this.remoteExtension = remoteExtension;
        this.projectName = projectName;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.remoteUser = remoteUser;
        this.remotePassword = remotePassword;
        this.remotePath = remotePath;
        this.localPath = localPath;
    }

    public JobInfo(int id, String jobType, String operationType, String localExtension, String remoteExtension, String projectName, String remoteHost, int remotePort, String remoteUser, String remotePassword, String remotePath, String localPath) {
        this.id = id;
        this.jobType = jobType;
        this.operationType = operationType;
        this.localExtension = localExtension;
        this.remoteExtension = remoteExtension;
        this.projectName = projectName;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.remoteUser = remoteUser;
        this.remotePassword = remotePassword;
        this.remotePath = remotePath;
        this.localPath = localPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getLocalExtension() {
        return localExtension;
    }

    public void setLocalExtension(String localExtension) {
        this.localExtension = localExtension;
    }

    public String getRemoteExtension() {
        return remoteExtension;
    }

    public void setRemoteExtension(String remoteExtension) {
        this.remoteExtension = remoteExtension;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public String getRemotePassword() {
        return remotePassword;
    }

    public void setRemotePassword(String remotePassword) {
        this.remotePassword = remotePassword;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
