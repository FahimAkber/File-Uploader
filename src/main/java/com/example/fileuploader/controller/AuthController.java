package com.example.fileuploader.controller;

import com.example.fileuploader.model.LoginRequest;
import com.example.fileuploader.model.UserInfo;
import com.example.fileuploader.service.AuthenticationService;
import com.example.fileuploader.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "create-user")
    public ResponseEntity<Object> createUser(@RequestBody UserInfo userInfo) throws Exception {
        return ResponseEntity.ok(authenticationService.saveUser(userInfo));
    }

    @PostMapping(value = "login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) throws Exception {
        System.out.println(loginRequest);
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }
}
