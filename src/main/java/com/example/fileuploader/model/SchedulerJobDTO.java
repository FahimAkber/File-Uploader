package com.example.fileuploader.model;

public class SchedulerJobDTO {
    private RequestTrigger requestTrigger;
    private String jobKey;

    public SchedulerJobDTO() {
    }

    public SchedulerJobDTO(RequestTrigger requestTrigger, String jobKey) {
        this.requestTrigger = requestTrigger;
        this.jobKey = jobKey;
    }

    public RequestTrigger getRequestTrigger() {
        return requestTrigger;
    }

    public void setRequestTrigger(RequestTrigger requestTrigger) {
        this.requestTrigger = requestTrigger;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }
}
