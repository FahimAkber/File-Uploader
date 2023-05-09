package com.example.fileuploader.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileUploaderExceptionHandler {

    @ExceptionHandler(value = FileUploaderException.class)
    public ResponseEntity<Object> exceptionResponse(FileUploaderException fileUploaderException){
        return new ResponseEntity<>(fileUploaderException.getErrorMessage(), fileUploaderException.getErrorCode());
    }

}
