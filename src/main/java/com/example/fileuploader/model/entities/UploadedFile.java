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
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "destination_host")
    private String destinationHost;
    @Column(name = "destination_path")
    private String destinationPath;
    private String status;
    @Column(name = "uploaded_date")
    private LocalDate uploadedDate;

    public UploadedFile(String fileName, String destinationHost, String destinationPath, String status, LocalDate uploadedDate){
        this.fileName = fileName;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.status = status;
        this.uploadedDate = uploadedDate;
    }
}
