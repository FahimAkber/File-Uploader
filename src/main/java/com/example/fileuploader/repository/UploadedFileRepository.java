package com.example.fileuploader.repository;

import com.example.fileuploader.model.KeyWiseValue;
import com.example.fileuploader.model.entities.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {
    @Query("SELECT UF.fileName FROM UploadedFile AS UF where UF.fileName IN :fileNames")
    List<String> findAllByFileNameList(@Param("fileNames") List<String> fileNames);

    @Query(value = "SELECT job_key as jobKey, GROUP_CONCAT(file_name) as fileName FROM uploaded_file WHERE status = :status GROUP BY job_key", nativeQuery = true)
    List<Map<String, String>> findKeyWiseFileByStatus(@Param("status") String status);

    @Modifying
    @Query("UPDATE UploadedFile UF SET UF.status = :status WHERE UF.fileName = :fileName")
    void updateFileStatus(@Param("fileName") String fileName, @Param("status") String status);

    UploadedFile findByFileName(String fileName);
}
