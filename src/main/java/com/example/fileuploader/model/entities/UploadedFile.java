package com.example.fileuploader.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "uploaded_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fileName;
    private String jobKey;
    private String status;
    private LocalDate uploadedDate;

    public UploadedFile(String fileName, String jobKey, String status, LocalDate uploadedDate){
        this.fileName = fileName;
        this.jobKey = jobKey;
        this.status = status;
        this.uploadedDate = uploadedDate;
    }
}
