package com.example.fileuploader.model;

public class RequestJob {
    private String jobKey;
    private String jobDescription;
    private int jobInformationId;

    public RequestJob(){}
    public RequestJob(String jobKey, String jobDescription, int jobInformationId){
        this.jobKey = jobKey;
        this.jobDescription = jobDescription;
        this.jobInformationId = jobInformationId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public int getJobInformationId() {
        return jobInformationId;
    }

    public void setJobInformationId(int jobInformationId) {
        this.jobInformationId = jobInformationId;
    }
}
