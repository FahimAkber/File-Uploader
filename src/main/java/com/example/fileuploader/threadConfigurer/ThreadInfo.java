package com.example.fileuploader.threadConfigurer;

import com.jcraft.jsch.ChannelSftp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThreadInfo {
    private String threadId;
    private List<String> folders;
    private String sourceHost;
    private int sourcePort;
    private String sourceUser;
    private String sourcePassword;
    private String sourceSecureFile;
    private String sourcePath;
    private String localBasePath;
    private String destinationHost;
    private String destinationPath;
    private String fileExtension;
}
