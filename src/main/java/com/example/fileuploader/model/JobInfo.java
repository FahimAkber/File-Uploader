package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInfo implements Serializable {
    @NonNull
    private Long  sourceServerId;
    @NonNull
    private String sourcePath;
    @NonNull
    private String fileExtension;
    @NonNull
    private Long destinationServerId;
    @NonNull
    private String destinationPath;
}












//