package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.QuartzJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuartzJobInfoRepository extends JpaRepository<QuartzJobInfo, Integer> {
}
