package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.LoginRequest;
import com.example.fileuploader.model.UserInfo;
import com.example.fileuploader.model.entities.User;
import com.example.fileuploader.repository.UserInfoRepository;
import com.example.fileuploader.service.AuthenticationService;
import com.example.fileuploader.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserInfoRepository userInfoRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationServiceImpl(UserInfoRepository userInfoRepository, JwtUtil jwtUtil) {
        this.userInfoRepository = userInfoRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User saveUser(UserInfo userInfo) throws Exception {
        User userExists = userInfoRepository.findByEmail(userInfo.getEmail());

        if (Objects.nonNull(userExists)) {
            throw new Exception("User with this email exists!!");
        }

        User user = User.builder()
                .email(userInfo.getEmail())
                .role(userInfo.getRole())
                .build();

        return userInfoRepository.save(user);
    }

    @Override
    public String login(LoginRequest loginRequest) throws Exception {
        User user = userInfoRepository.findByEmail(loginRequest.getEmail());

        if (Objects.isNull(user)) {
            throw new Exception("User Not Exist!!");
        }

        return jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
    }
}
