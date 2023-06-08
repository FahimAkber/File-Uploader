package com.example.fileuploader.model.response;

import lombok.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomResponse {
    @NonNull
    private List<ServerInfoResponse> serverInfos;
    private Integer totalPage;
}
