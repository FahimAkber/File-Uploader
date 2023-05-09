package com.example.fileuploader.service.implementation;

import com.example.fileuploader.model.entities.UploadedFile;
import com.example.fileuploader.service.UploadedFileService;
import com.example.fileuploader.model.UploadedFileInfo;
import com.example.fileuploader.repository.UploadedFileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private List<UploadedFileInfo> convertFileList(List<UploadedFile> files) {
        List<UploadedFileInfo> fileInfos = new ArrayList<>();

        for (UploadedFile file : files) {
            fileInfos.add(modelMapper.map(file, UploadedFileInfo.class));
        }
        return fileInfos;
    }
}
