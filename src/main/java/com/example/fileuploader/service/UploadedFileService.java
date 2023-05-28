package com.example.fileuploader.service;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.model.UploadedFileInfo;

import java.util.List;
import java.util.Map;

public interface UploadedFileService {
    void save(UploadedFile uploadedFile);
    boolean isUploaded(String fileName);
    List<UploadedFileInfo> getFiles();
    List<String> getCheckedFiles(List<String> fileNames);

    List<UploadedFileInfo> getFilesByStatusAndCriteria(String status);
    void updateStatusOfFile(String fileName, String status);
}
