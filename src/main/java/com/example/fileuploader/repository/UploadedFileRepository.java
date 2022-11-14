package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {
    @Query("SELECT UF.fileName FROM UploadedFile AS UF where UF.fileName IN :fileNames")
    List<String> findAllByFileNameList(@Param("fileNames") List<String> fileNames);

    UploadedFile findByFileName(String fileName);
}
