package com.example.fileuploader.exceptions;

import org.springframework.http.HttpStatus;

public class FileUploaderException extends RuntimeException {

    private String errorMessage;
    private HttpStatus errorCode;

    public FileUploaderException(String errorMessage, HttpStatus errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(HttpStatus errorCode) {
        this.errorCode = errorCode;
    }
}
