package com.example.fileuploader.service;

import com.example.fileuploader.model.LoginRequest;
import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.model.UserInfo;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.entities.User;

public interface AuthenticationService {
    User saveUser(UserInfo userInfo) throws Exception;

    String login(LoginRequest loginRequest) throws Exception;
}
