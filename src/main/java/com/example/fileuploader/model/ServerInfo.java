package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
    private String host;
    private int port;
    private String user;
    private String password;
    private MultipartFile secureFile;
}
