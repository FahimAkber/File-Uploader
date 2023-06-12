package com.example.fileuploader.controller;

import com.example.fileuploader.model.UserInfo;
import com.example.fileuploader.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "create-user")
    public ResponseEntity<Object> saveJobInfo(@RequestBody UserInfo userInfo) throws Exception {
        return ResponseEntity.ok(authenticationService.saveUser(userInfo));
    }
}
