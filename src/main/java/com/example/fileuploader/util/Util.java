package com.example.fileuploader.util;

import com.example.fileuploader.exceptions.FileUploaderException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class Util {
    public static void checkRequiredField(String fieldName, String value) throws Exception{
        if(value == null || value.trim().equals("")){
            throw new Exception(fieldName + " can't be null or empty string.");
        }
    }

    public static void checkRequiredFile(String fileName, MultipartFile file) throws Exception{
        if(file == null || file.isEmpty()){
            throw new Exception(fileName + "can't be null or empty");
        }else if(file.getSize() > 10240){
            throw new Exception(fileName + "is too long.");
        }
    }
    public static Pageable getPageableObject(Integer pageNo, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return pageable;
    }
}
