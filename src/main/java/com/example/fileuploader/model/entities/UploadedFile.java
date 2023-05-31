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

    @Column(name = "source_host")
    private String sourceHost;
    @Column(name = "source_path")
    private String sourcePath;
    @Column(name = "file_creation_date")
    private Date fileCreationDate;
    @Column(name = "source_file_size")
    private byte sourceFileSize;
    @Column(name = "local_action_date")
    private Date localActionDate;
    @Column(name = "destination_host")
    private String destinationHost;
    @Column(name = "destination_path")
    private String destinationPath;
    @Column(name = "destination_file_size")
    private byte destinationFileSize;
    private String description;
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
