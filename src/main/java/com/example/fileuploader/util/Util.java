package com.example.fileuploader.util;

import com.example.fileuploader.exceptions.FileUploaderException;
import com.example.fileuploader.threadConfigurer.PoolInstance;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.Executors;

public class Util {
    public static final PoolInstance poolInstance = new PoolInstance(Executors.newFixedThreadPool(20));
    public static void checkRequiredField(String fieldName, String value) throws Exception{
        if(value == null || value.trim().equals("")){
            throw new Exception(fieldName + " can't be null or empty string.");
        }
    }

    public static void checkRequiredFile(String fileName, MultipartFile file) throws Exception{
        if(file == null || file.isEmpty()){
            throw new Exception(fileName + "can't be null or empty");
        }else if(file.getSize() > 10240){
            throw new Exception(fileName + "is too long.");
        }
    }
    public static Pageable getPageableObject(Integer pageNo, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return pageable;
    }
    public static File createDirIfNotExist(String rootPath){
        try{
            File file = new File(rootPath);
            if(!file.exists()){
                file.mkdirs();
            }
            return file;
        }catch (Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public static Session createSession(String remoteUser, String remoteHost, int remotePort, String fileName, String password) {
        Session session = null;
        JSch jSch = new JSch();
        try {
            if(fileName != null && !fileName.trim().isEmpty()){
                jSch.addIdentity(fileName);
                session = jSch.getSession(remoteUser, remoteHost, remotePort);
            }else{
                session = jSch.getSession(remoteUser, remoteHost, remotePort);
                session.setPassword(password);
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("compression.s2c", "zlib,none");
            session.setConfig("compression.c2s", "zlib,none");
            session.setConfig("rcvbuf", "1048576"); // 1 MB
            session.setConfig("sndbuf", "1048576");
            session.setConfig("sftp.max_packet", "131072"); // Set maximum packet size to 131072 bytes (128 KB)

            session.connect();
        } catch (JSchException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception exception){
            throw new FileUploaderException(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return session;
    }
    public static ChannelSftp createChannelSftp(Session session) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.setBulkRequests(30);
            channelSftp.connect();
        } catch (JSchException | NullPointerException e) {
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return channelSftp;
    }
    public static void destroyConnection(Session session, ChannelSftp channelSftp) {
        try{
            if(channelSftp != null){
                channelSftp.disconnect();
            }
            if(session != null){
                session.disconnect();
            }
        }catch (Exception e){
            throw new FileUploaderException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
