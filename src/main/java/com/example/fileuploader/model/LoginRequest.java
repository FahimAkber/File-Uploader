package com.example.fileuploader.model;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {
    @Email(message = "Invalid email address")
    public String email;
}
