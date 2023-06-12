package com.example.fileuploader.repository;

import com.example.fileuploader.model.entities.Server;
import com.example.fileuploader.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
