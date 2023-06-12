package com.example.fileuploader.model;

public enum UserRole {
    SUPER_ADMIN("SuperAdmin"),
    ADMIN("Admin"),
    NORMAL_USER("NormalUser");

    public final String value;
    UserRole(String value){
        this.value = value;
    }
}
