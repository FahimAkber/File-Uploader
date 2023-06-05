package com.example.fileuploader.controller;

import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.service.ServerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/server")
public class ServerController {
    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @PostMapping(value = "/configurer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> configureServer(@RequestPart("serverInfo") ServerInfo serverInfo, @RequestPart("secureFile") MultipartFile secureFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        serverInfo.setSecureFile(secureFile);
        return new ResponseEntity<>(serverService.saveServerInfo(serverInfo), headers, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getServers(@RequestParam("page") Integer pageNo, @RequestParam("size") Integer pageSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serverService.getServerInfos(pageNo, pageSize), headers, HttpStatus.OK);
    }

    @PutMapping(value = "/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> editServer(@RequestPart("id") Long id, @RequestPart("serverInfo") ServerInfo serverInfo, @RequestPart("secureFile") MultipartFile secureFile){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        serverInfo.setSecureFile(secureFile);

        return new ResponseEntity<>(serverService.editServerInfo(id, serverInfo), headers, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteServer(@PathVariable("id") Long id){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(serverService.deleteServerInfo(id), headers, HttpStatus.OK);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> findServerByHost(@RequestParam("host") String host){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serverService.findByHost(host), headers, HttpStatus.OK);
    }

}
