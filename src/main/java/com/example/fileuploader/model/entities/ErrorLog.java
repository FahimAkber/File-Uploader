package com.example.fileuploader.model.entities;

import com.fasterxml.jackson.databind.DatabindException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "error_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String sourceHost;
    private String sourcePath;
    private String destinationHost;
    private String destinationPath;
    private String errorCause;
    private Date errorDate;

}
