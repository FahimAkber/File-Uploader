package com.example.fileuploader.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "server_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    private String host;
    private int port;
    private String user;
    private String password;
    private String secureFileName;
}
