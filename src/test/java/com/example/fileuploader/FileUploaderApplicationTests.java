package com.example.fileuploader;

import com.example.fileuploader.controller.FileTransferController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileUploaderApplicationTests {

    @Autowired
    FileTransferController controller;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(controller);
    }

}
