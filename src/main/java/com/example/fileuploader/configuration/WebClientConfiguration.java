package com.example.fileuploader.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    WebClient provideWebClient(){
        return WebClient.builder().build();
    }
}
