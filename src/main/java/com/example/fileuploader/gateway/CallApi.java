package com.example.fileuploader.gateway;

import com.example.fileuploader.threadConfigurer.ThreadInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CallApi {
    private final WebApiClient webApiClient;

    public CallApi(WebApiClient webApiClient) {
        this.webApiClient = webApiClient;
    }

    public String collectFiles(List<ThreadInfo> tasks, String api){
        return webApiClient.callPost(api, tasks, String.class);
    }
}
