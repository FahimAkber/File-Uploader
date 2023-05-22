package com.example.fileuploader.model;

public enum Status {
    RECEIVED("Received"),
    PROCESSED("Processed"),
    SENT("Sent");

    public final String value;
    Status(String value){
        this.value = value;
    }
}
