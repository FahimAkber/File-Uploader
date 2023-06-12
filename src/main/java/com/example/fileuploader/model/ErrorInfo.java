package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.JobDetail;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {
    private String fileName;
    private String sourceHost;
    private String sourcePath;
    private String destinationHost;
    private String destinationPath;
    private String errorCause;
    private Date errorDate;
}
