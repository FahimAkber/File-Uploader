package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findByHost(String host);
}
