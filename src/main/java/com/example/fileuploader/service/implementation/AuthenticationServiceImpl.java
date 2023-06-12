package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.UserInfo;
import com.example.fileuploader.model.entities.User;
import com.example.fileuploader.repository.UserInfoRepository;
import com.example.fileuploader.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserInfoRepository userInfoRepository;

    public AuthenticationServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
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
}
