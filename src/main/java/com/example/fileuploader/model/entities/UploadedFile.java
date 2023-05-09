package com.example.fileuploader.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "uploaded_file")
public class UploadedFile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fileName;

    public UploadedFile(){

    }

    public UploadedFile(String fileName) {
        this.fileName = fileName;
    }

    public UploadedFile(int id, String fileName) {
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
