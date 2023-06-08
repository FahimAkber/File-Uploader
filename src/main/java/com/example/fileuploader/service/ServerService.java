package com.example.fileuploader.service;

import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.model.response.CustomResponse;
import com.example.fileuploader.model.response.MessageResponse;
import com.example.fileuploader.model.response.ServerInfoResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ServerService {
    Server saveServerInfo(ServerInfo serverInfo);
    CustomResponse getServerInfos(Integer pageNo, Integer pageSize);
    Server findById(Long id) throws Exception;
    Server findByHost(String host);
    Server editServerInfo(Long id, ServerInfo serverInfo);
    MessageResponse deleteServerInfo(Long id);


}
