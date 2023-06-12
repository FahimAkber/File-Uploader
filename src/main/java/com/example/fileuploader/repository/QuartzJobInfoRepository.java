package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.QuartzJobInfo;
import com.example.fileuploader.model.entities.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartzJobInfoRepository extends JpaRepository<QuartzJobInfo, Integer> {
    QuartzJobInfo findByJobKey(String jobKey);

    Page<QuartzJobInfo> findAllBySourceServerHostOrDestinationServerHost(String sourceServerHost, String destinationServerHost, Pageable pageable);

    @Query(value = "SELECT * FROM quartz_job_configuration WHERE source_server = :sourceServerId AND source_path = :sourceServerPath AND destination_server = :destinationServerId AND destination_path = :destinationServerPath", nativeQuery = true)
    QuartzJobInfo findBySourceAndDestination(Long sourceServerId, String sourceServerPath, Long destinationServerId, String destinationServerPath);
}
