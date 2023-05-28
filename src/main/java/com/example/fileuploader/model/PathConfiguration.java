package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathConfiguration implements Serializable {
    @NonNull
    private String sourcePath;
    private String fileExtension;
    @NonNull
    private Long destinationServerId;
    @NonNull
    private String destinationPath;
}
