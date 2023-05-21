package com.example.fileuploader.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="quartz_job_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartzJobInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String jobKey;
    private String fileExtension;
    private String sourceHost;
    private int sourcePort;
    private String sourceUser;
    private String sourceFileName;
    private String sourcePath;
    private String destinationHost;
    private int destinationPort;
    private String destinationUser;
    private String destinationFileName;
    private String destinationPath;
    private String jobGroup;
}
