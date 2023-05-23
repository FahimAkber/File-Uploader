package com.example.fileuploader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    @Value("${jsch.configuration.key}")
    private String configurationKey;
    @Value("${jsch.configuration.value}")
    private String configurationValue;
    @Value("${channel.type}")
    private String channelType;
    @Value("${secure.file.location}")
    private String secureFileLocation;
    @Value("${local.file.location}")
    private String localFileLocation;

}
