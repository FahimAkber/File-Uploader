package com.example.fileuploader.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfoResponse {
    public Long id;
    private String host;
    private int port;
    private String user;
    private String secureFileName;
}
