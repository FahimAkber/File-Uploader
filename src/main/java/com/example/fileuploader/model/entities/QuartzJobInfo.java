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
    private String operationType;
    private String fileExtension;
    private String remoteHost;
    private int remotePort;
    private String remoteUser;
    private String remotePath;
    private String localPath;
    private String fileName;
    private String jobGroup;
}
