//package com.example.fileuploader.service;
//
//import com.example.fileuploader.model.entities.UploadedFile;
//import com.example.fileuploader.repository.UploadedFileRepository;
//import com.example.fileuploader.service.implementation.UploadedFileServiceImpl;
//import com.example.fileuploader.model.UploadedFileInfo;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.modelmapper.ModelMapper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UploadFileTestService {
//
//    @Spy
//    @InjectMocks
//    UploadedFileServiceImpl uploadedFileService;
//
//    @Mock
//    UploadedFileRepository uploadedFileRepository;
//
//    @Mock
//    ModelMapper modelMapper;
//
//    @Mock
//    UploadedFile uploadedFile;
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void notNull(){
//        Assertions.assertNotNull(uploadedFileService);
//    }
//
//    @Test
//    void save(){
//        assertDoesNotThrow(()->
//                    uploadedFileService.save(uploadedFile)
//        );
//    }
//
//    @Test
//    void isUploaded(){
//        String fileName = "file.txt";
//        Mockito.when(uploadedFileRepository.findByFileName(fileName)).thenReturn(null);
//
//        Boolean aBoolean = assertDoesNotThrow(() ->
//                uploadedFileService.isUploaded(fileName)
//        );
//
//        assertEquals(aBoolean, false);
//
//    }
//
//    @Test
//    void getFiles(){
//        List<UploadedFileInfo> uploadedFileInfos = assertDoesNotThrow(() -> uploadedFileService.getFiles());
//        assertEquals(0, uploadedFileInfos.size());
//    }
//
//    @Test
//    void getCheckedFiles(){
//        List<String> files = new ArrayList<>();
//        Mockito.when(uploadedFileRepository.findAllByFileNameList(files)).thenReturn(files);
//        Mockito.doReturn(files).when(uploadedFileService).getFiles();
//
//        List<String> uploadedFileInfos = assertDoesNotThrow(() -> uploadedFileService.getCheckedFiles(files));
//        assertEquals(files.size(), uploadedFileInfos.size());
//    }
//}
