package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerRequest {
    private String jobKey;
    private int totalInterval;
    private int frequency;
    private Date startAt;
}
