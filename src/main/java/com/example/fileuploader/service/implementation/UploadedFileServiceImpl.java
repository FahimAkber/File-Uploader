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
    public List<String> getCheckedFiles(List<String> fileNames, String sourceHost, String sourcePath, String destinationHost, String destinationPath) {
        return uploadedFileRepository.findAllByFileNameList(fileNames, sourceHost, sourcePath, destinationHost, destinationPath);
    }

    @Override
    public List<UploadedFileInfo> getFilesByStatusAndCriteria(String status) {
        try{
            List<Object[]> filesByStatusAndCriteria = uploadedFileRepository.findByStatusAndCriteria(status);
            List<UploadedFileInfo> files = new ArrayList<>();
            for (Object[] result : filesByStatusAndCriteria) {
                String destinationHost = (String) result[0];
                String destinationPath = (String) result[1];
                String fileName = (String) result[2];

                UploadedFileInfo uploadedFileInfo = new UploadedFileInfo(destinationHost, destinationPath, Arrays.asList(fileName.split(",")));
                files.add(uploadedFileInfo);
            }

            return files;
        }catch (Exception exception){
            throw exception;
        }
    }

    @Override
    public void updateStatusOfFile(String fileName, String status, Date updatedDate) {
        try {
            UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName);
            uploadedFile.setDestinationUploadedDate(updatedDate);
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
