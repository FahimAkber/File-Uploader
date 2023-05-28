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
    private String jobGroup;
    private String fileExtension;

    @OneToOne
    @JoinColumn(name = "source_server")
    private Server sourceServer;

    private String sourcePath;

    @OneToOne
    @JoinColumn(name = "destination_server")
    private Server destinationServer;

    private String destinationPath;
}
