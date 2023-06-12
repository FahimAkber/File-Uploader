package com.example.fileuploader.service;

import com.example.fileuploader.model.ErrorInfo;
import com.example.fileuploader.model.response.MessageResponse;
import org.springframework.data.domain.Page;

public interface ErrorLogService {
    MessageResponse saveErrorLog(ErrorInfo errorInfo);
    Page<ErrorInfo> getErrorInfos(Integer page, Integer size);
    ErrorInfo getErrorInfoByFileName(String fileName);
    Page<ErrorInfo> getErrorInfosByHost(String source, Integer page, Integer size);
    Page<ErrorInfo> getErrorInfosByPath(String path, Integer page, Integer size);
}
