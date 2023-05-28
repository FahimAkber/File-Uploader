package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.KeyWiseValue;
import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.model.UploadedFileInfo;
import com.example.fileuploader.repository.UploadedFileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final ModelMapper modelMapper;

    public UploadedFileServiceImpl(UploadedFileRepository uploadedFileRepository, ModelMapper modelMapper) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void save(UploadedFile uploadedFile) {
        uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public boolean isUploaded(String fileName) {
        UploadedFile file = uploadedFileRepository.findByFileName(fileName);
        return file != null;
    }

    @Override
    public List<UploadedFileInfo> getFiles() {
        List<UploadedFile> files = uploadedFileRepository.findAll();
        return convertFileList(files);
    }

    @Override
    public List<String> getCheckedFiles(List<String> fileNames) {
        return uploadedFileRepository.findAllByFileNameList(fileNames);
    }

    @Override
    public List<UploadedFileInfo> getFilesByStatusAndCriteria(String status) {
        try{
            List<UploadedFileInfo> filesByStatusAndCriteria = uploadedFileRepository.findByStatusAndCriteria(status);
            filesByStatusAndCriteria.stream().forEach(uploadedFileInfo ->
                    uploadedFileInfo.setFileNames(Arrays.asList(uploadedFileInfo.getFileName().split(",")))
            );

            return filesByStatusAndCriteria;
        }catch (Exception exception){
            throw exception;
        }
    }

    @Override
    public void updateStatusOfFile(String fileName, String status) {
        try {
            UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName);
            uploadedFile.setStatus(status);
            uploadedFileRepository.save(uploadedFile);
        }catch (Exception exception){
            throw exception;
        }
    }

    private List<UploadedFileInfo> convertFileList(List<UploadedFile> files) {
        List<UploadedFileInfo> fileInfos = new ArrayList<>();

        for (UploadedFile file : files) {
            fileInfos.add(modelMapper.map(file, UploadedFileInfo.class));
        }
        return fileInfos;
    }
}
