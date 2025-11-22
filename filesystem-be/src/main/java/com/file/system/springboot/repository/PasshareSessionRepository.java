package com.file.system.springboot.repository;

import com.file.system.springboot.model.PasshareSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasshareSessionRepository extends JpaRepository<PasshareSession, Long> {
    Optional<PasshareSession> findByCode(String code);
    Optional<PasshareSession> findByCodeAndActiveTrue(String code);
}

