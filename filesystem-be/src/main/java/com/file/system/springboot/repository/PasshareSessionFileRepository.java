package com.file.system.springboot.repository;

import com.file.system.springboot.model.PasshareSessionFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasshareSessionFileRepository extends JpaRepository<PasshareSessionFile, Long> {
    List<PasshareSessionFile> findBySessionId(Long sessionId);
}

