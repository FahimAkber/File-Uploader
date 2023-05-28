package com.example.fileuploader.service;

import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.model.response.ServerInfoResponse;

import java.util.List;

public interface ServerService {
    Server saveServerInfo(ServerInfo serverInfo);
    List<ServerInfoResponse> getServerInfos();
    Server findById(Long id) throws Exception;
    Server findByHost(String host);


}
