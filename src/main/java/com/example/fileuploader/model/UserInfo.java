package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserInfo {
    @NonNull
    @Email(message = "Invalid email address")
    public String email;

    @NonNull
    @Pattern(regexp = "ADMIN|NORMAL_USER", message = "Invalid role")
    public UserRole role;
}
