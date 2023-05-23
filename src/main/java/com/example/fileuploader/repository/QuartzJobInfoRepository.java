package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.QuartzJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartzJobInfoRepository extends JpaRepository<QuartzJobInfo, Integer> {
    List<QuartzJobInfo> findByJobGroup(String jobGroup);
    QuartzJobInfo findByJobKey(String jobKey);
}
