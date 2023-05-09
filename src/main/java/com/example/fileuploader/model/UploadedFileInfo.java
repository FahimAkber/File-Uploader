package com.example.fileuploader.model;

public class UploadedFileInfo {
    private int id;
    private String fileName;

    public UploadedFileInfo() {
    }

    public UploadedFileInfo(String fileName) {
        this.fileName = fileName;
    }

    public UploadedFileInfo(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
