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
    @Column(name = "source_file_size")
    private Long sourceFileSize;
    @Column(name = "local_uploaded_date")
    private Date localUploadedDate;
    @Column(name = "local_path")
    private String localPath;
    @Column(name = "local_uploaded_file_size")
    private Long localUploadedFileSize;
    @Column(name = "destination_host")
    private String destinationHost;
    @Column(name = "destination_path")
    private String destinationPath;
    @Column(name = "destination_file_size")
    private Long destinationFileSize;
    @Column(name = "destination_uploaded_date")
    private Date destinationUploadedDate;
    private String description;
    private String status;

    public UploadedFile(String fileName, String sourceHost, String sourcePath, Long sourceFileSize,
                        Date localUploadedDate, String localPath, Long localUploadedFileSize,
                        String destinationHost, String destinationPath, String status){
        this.fileName = fileName;
        this.sourceHost = sourceHost;
        this.sourcePath = sourcePath;
        this.sourceFileSize = sourceFileSize;
        this.localUploadedDate = localUploadedDate;
        this.localPath = localPath;
        this.localUploadedFileSize = localUploadedFileSize;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.status = status;
    }


    public UploadedFile(String fileName, String sourceHost, String sourcePath, Long sourceFileSize,
                        Date localUploadedDate, String localPath, Long localUploadedFileSize,
                        String destinationHost, String destinationPath, Date destinationUploadedDate, String status){
        this.fileName = fileName;
        this.sourceHost = sourceHost;
        this.sourcePath = sourcePath;
        this.sourceFileSize = sourceFileSize;
        this.localUploadedDate = localUploadedDate;
        this.localPath = localPath;
        this.localUploadedFileSize = localUploadedFileSize;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.destinationUploadedDate = destinationUploadedDate;
        this.status = status;
    }

    public UploadedFile(String fileName, String sourceHost, String sourcePath, Long sourceFileSize,
                        Date localUploadedDate, String localPath, Long localUploadedFileSize,
                        String destinationHost, String destinationPath, Date destinationUploadedDate, Long destinationFileSize, String status){
        this.fileName = fileName;
        this.sourceHost = sourceHost;
        this.sourcePath = sourcePath;
        this.sourceFileSize = sourceFileSize;
        this.localUploadedDate = localUploadedDate;
        this.localPath = localPath;
        this.localUploadedFileSize = localUploadedFileSize;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.destinationUploadedDate = destinationUploadedDate;
        this.destinationFileSize = destinationFileSize;
        this.status = status;
    }

    public UploadedFile(String fileName, String sourceHost, String sourcePath, Long actualSize, String localPath, String destinationHost, String destinationPath, String status, String description) {
        this.fileName = fileName;
        this.sourceHost = sourceHost;
        this.sourcePath = sourcePath;
        this.localPath = localPath;
        this.destinationHost = destinationHost;
        this.destinationPath = destinationPath;
        this.status = status;
    }
}
