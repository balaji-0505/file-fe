package com.file.system.springboot.repository;

import com.file.system.springboot.model.PasshareSessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasshareSessionParticipantRepository extends JpaRepository<PasshareSessionParticipant, Long> {
    List<PasshareSessionParticipant> findBySessionId(Long sessionId);
    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);
}

