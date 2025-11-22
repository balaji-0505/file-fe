package com.file.system.springboot.service;

import com.file.system.springboot.model.*;
import com.file.system.springboot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PasshareSessionService {
    private final PasshareSessionRepository sessionRepository;
    private final PasshareSessionParticipantRepository participantRepository;
    private final PasshareSessionFileRepository sessionFileRepository;
    private final UserRepository userRepository;
    private final FileItemRepository fileItemRepository;
    private final Random random = new Random();

    public PasshareSessionService(
            PasshareSessionRepository sessionRepository,
            PasshareSessionParticipantRepository participantRepository,
            PasshareSessionFileRepository sessionFileRepository,
            UserRepository userRepository,
            FileItemRepository fileItemRepository) {
        this.sessionRepository = sessionRepository;
        this.participantRepository = participantRepository;
        this.sessionFileRepository = sessionFileRepository;
        this.userRepository = userRepository;
        this.fileItemRepository = fileItemRepository;
    }

    @Transactional
    public PasshareSession createSession(Long creatorId) {
        PasshareSession session = new PasshareSession();
        session.setCreatorId(creatorId);
        session.setCode(generateUniqueCode());
        session.setCreatedAt(Instant.now());
        session.setExpiresAt(Instant.now().plusSeconds(3600)); // 1 hour expiry
        session.setActive(true);

        // Save session first to get the ID
        session = sessionRepository.save(session);

        // Add creator as participant after session is saved
        PasshareSessionParticipant creatorParticipant = new PasshareSessionParticipant();
        creatorParticipant.setSession(session);
        creatorParticipant.setUserId(creatorId);
        creatorParticipant.setJoinedAt(Instant.now());
        participantRepository.save(creatorParticipant);

        return session;
    }

    @Transactional
    public PasshareSession joinSession(String code, Long userId) {
        PasshareSession session = sessionRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() -> new IllegalArgumentException("Session not found or inactive"));

        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(Instant.now())) {
            session.setActive(false);
            sessionRepository.save(session);
            throw new IllegalArgumentException("Session has expired");
        }

        // Check if user is already a participant
        if (!participantRepository.existsBySessionIdAndUserId(session.getId(), userId)) {
            PasshareSessionParticipant participant = new PasshareSessionParticipant();
            participant.setSession(session);
            participant.setUserId(userId);
            participant.setJoinedAt(Instant.now());
            participantRepository.save(participant);
        }

        return session;
    }

    @Transactional
    public PasshareSessionFile shareFile(Long sessionId, Long fileId, Long userId) {
        PasshareSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.isActive()) {
            throw new IllegalArgumentException("Session is not active");
        }

        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("User is not a participant in this session");
        }

        FileItem fileItem = fileItemRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Check if file is already shared in this session
        List<PasshareSessionFile> existingFiles = sessionFileRepository.findBySessionId(sessionId);
        Optional<PasshareSessionFile> existing = existingFiles.stream()
                .filter(sf -> sf.getFileItem().getId().equals(fileId))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get();
        }

        PasshareSessionFile sessionFile = new PasshareSessionFile();
        sessionFile.setSession(session);
        sessionFile.setFileItem(fileItem);
        sessionFile.setSharedByUserId(userId);
        sessionFile.setSharedAt(Instant.now());

        return sessionFileRepository.save(sessionFile);
    }

    public Optional<PasshareSession> getSession(Long sessionId, Long userId) {
        Optional<PasshareSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Optional.empty();
        }

        PasshareSession session = sessionOpt.get();
        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            return Optional.empty();
        }

        return sessionOpt;
    }

    public List<PasshareSessionFile> getSessionFiles(Long sessionId, Long userId) {
        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("User is not a participant in this session");
        }

        return sessionFileRepository.findBySessionId(sessionId);
    }

    public List<PasshareSessionParticipant> getSessionParticipants(Long sessionId, Long userId) {
        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("User is not a participant in this session");
        }

        return participantRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void leaveSession(Long sessionId, Long userId) {
        PasshareSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // Don't allow creator to leave (they can end the session instead)
        if (session.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("Creator cannot leave session. End the session instead.");
        }

        List<PasshareSessionParticipant> participants = participantRepository.findBySessionId(sessionId);
        participants.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .ifPresent(participantRepository::delete);
    }

    public PasshareSessionFile getSessionFile(Long sessionId, Long sessionFileId, Long userId) {
        PasshareSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("User is not a participant in this session");
        }

        PasshareSessionFile sessionFile = sessionFileRepository.findById(sessionFileId)
                .orElseThrow(() -> new IllegalArgumentException("Shared file not found"));

        if (!sessionFile.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("File does not belong to this session");
        }

        return sessionFile;
    }

    @Transactional
    public void incrementSessionFileDownloadCount(Long sessionId, Long sessionFileId, Long userId) {
        PasshareSessionFile sessionFile = getSessionFile(sessionId, sessionFileId, userId);
        sessionFile.setDownloadCount(sessionFile.getDownloadCount() + 1);
        sessionFileRepository.save(sessionFile);
    }

    @Transactional
    public void removeSharedFile(Long sessionId, Long sessionFileId, Long userId) {
        PasshareSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.isActive()) {
            throw new IllegalArgumentException("Session is not active");
        }

        // Verify user is a participant
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("User is not a participant in this session");
        }

        PasshareSessionFile sessionFile = sessionFileRepository.findById(sessionFileId)
                .orElseThrow(() -> new IllegalArgumentException("Shared file not found"));

        if (!sessionFile.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("File does not belong to this session");
        }

        // Allow removal if user shared the file OR user is the session creator
        if (!sessionFile.getSharedByUserId().equals(userId) && !session.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("Only the person who shared the file or the session creator can remove it");
        }

        sessionFileRepository.delete(sessionFile);
    }

    @Transactional
    public void endSession(Long sessionId, Long userId) {
        PasshareSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("Only the creator can end the session");
        }

        session.setActive(false);
        sessionRepository.save(session);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (sessionRepository.findByCode(code).isPresent());
        return code;
    }

    private String generateCode() {
        // Generate 8-character alphanumeric code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}

