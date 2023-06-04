//package com.example.fileuploader.repository;
//
//import com.example.fileuploader.model.entities.UploadedFile;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//
//
//@SpringBootTest
//public class UploadedFileTestRepo {
//
//    @Mock
//    private UploadedFileRepository fileRepository;
//
//
//    @Test
//    void findFileByName(){
//        UploadedFile byFileName = fileRepository.findByFileName("x.txt");
//        assertThat(byFileName == null).isTrue();
//    }
//
//    @Test
////    void findAllByFileNameList(){
////        List<String> list = new ArrayList<>();
////        Mockito.when(fileRepository.findAllByFileNameList(list)).thenReturn(null);
////        assertDoesNotThrow(()-> fileRepository.findAllByFileNameList(list));
////    }
//}
