package com.example.fileuploader.gateway;

import com.example.fileuploader.exceptions.FileUploaderException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebApiClient {
    private final WebClient webClient;

    public WebApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <E, T> T callPost(String url, E body, Class<T> responseType) {
        try {
            return webClient.post()
                    .uri(url)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, res ->
                            res.bodyToMono(Exception.class)
                                    .flatMap(error ->
                                            Mono.error(new FileUploaderException(error.getMessage(), HttpStatus.BAD_REQUEST))
                                    ))
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        throw new FileUploaderException("Something went wrong", HttpStatus.BAD_REQUEST);
                    })
                    .bodyToMono(responseType)
                    .block();
        } catch (FileUploaderException e){
            throw e;
        }
        catch (Exception e) {
            throw new FileUploaderException("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }

    public <T> T callGet(String url, Class<T> responseType) {
        try {
            return webClient.get()
                    .uri(url)
                    .header("AUTH-KEY", "40WiKUZe3cH826NPsypgfuKJvKSZBdkX")
                    .header("STK-CODE", "FSIBL")
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, res ->
                            res.bodyToMono(Exception.class)
                                    .flatMap(error ->
                                            Mono.error(new FileUploaderException(error.getMessage(), HttpStatus.BAD_REQUEST))
                                    ))
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        throw new FileUploaderException("Something went wrong", HttpStatus.BAD_REQUEST);
                    })
                    .bodyToMono(responseType)
                    .block();

        }
        catch (FileUploaderException e) {
            throw e;
        } catch (Exception e) {
            throw new FileUploaderException("Something went wrong", HttpStatus.BAD_REQUEST);
        }
    }
}
