package com.example.fileuploader.service;

import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.model.UploadedFileInfo;

import java.util.List;

public interface UploadedFileService {
    void save(UploadedFile uploadedFile);
    boolean isUploaded(String fileName);
    List<UploadedFileInfo> getFiles();
    List<String> getCheckedFiles(List<String> fileNames);
}
