package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileInfo {
    private int id;
    private String destinationHost;
    private String destinationPath;
    private String fileName;
    private List<String> fileNames;
}
