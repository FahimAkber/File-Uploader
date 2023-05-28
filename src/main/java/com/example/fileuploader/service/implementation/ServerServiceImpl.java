package com.example.fileuploader.service.implementation;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.model.Configuration;
import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.ServerInfo;
import com.example.fileuploader.model.response.ServerInfoResponse;
import com.example.fileuploader.repository.ServerRepository;
import com.example.fileuploader.service.ServerService;
import com.example.fileuploader.util.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final Configuration configuration;

    public ServerServiceImpl(ServerRepository serverRepository, Configuration configuration) {
        this.serverRepository = serverRepository;
        this.configuration = configuration;
    }

    @Override
    public Server saveServerInfo(ServerInfo serverInfo) {
        try {
            Util.checkRequiredField("Host", serverInfo.getHost());
            Util.checkRequiredField("User", serverInfo.getUser());
            if((serverInfo.getPassword() == null
                    || serverInfo.getPassword().trim().isEmpty())
                    && (serverInfo.getSecureFile() == null
                    || serverInfo.getSecureFile().getSize() <= 0)){
                throw new Exception("Security Credential Required");
            }
            Server server = new Server();
            BeanUtils.copyProperties(serverInfo, server);

            if(serverInfo.getPassword() == null || serverInfo.getPassword().trim().isEmpty()){
                String secureFile = uploadFileToLocal(serverInfo.getSecureFile());
                server.setSecureFileName(secureFile);
            }

            return serverRepository.save(server);
        } catch (Exception e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<ServerInfoResponse> getServerInfos() {
        try{
            return serverRepository.findAll().stream().map(server -> new ServerInfoResponse(server.getId(), server.getHost(), server.getPort(), server.getUser(), server.getSecureFileName())).collect(Collectors.toList());
        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Server findById(Long id) throws Exception {
        Optional<Server> serverById = serverRepository.findById(id);
        if(serverById.isPresent()){
            return serverById.get();
        }else{
            throw new Exception("Server not found");
        }
    }

    @Override
    public Server findByHost(String host) {
        return serverRepository.findByHost(host);
    }

    private String generateFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + extension;
    }

    private String uploadFileToLocal(MultipartFile multipartFile) throws Exception {

        String fileName = generateFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));;
        Path path = Paths.get(configuration.getSecureFileLocation(), fileName);

        if(!Files.exists(path.getParent())){
            Files.createDirectory(path.getParent());
        }

        multipartFile.transferTo(path);
        return path.toAbsolutePath().toString();
    }

}
