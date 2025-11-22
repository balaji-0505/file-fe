package com.file.system.springboot.controller;

import com.file.system.springboot.model.FileItem;
import com.file.system.springboot.model.PasshareSession;
import com.file.system.springboot.model.PasshareSessionFile;
import com.file.system.springboot.model.PasshareSessionParticipant;
import com.file.system.springboot.service.PasshareSessionService;
import com.file.system.springboot.service.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/passhare")
@CrossOrigin(origins = "*")
public class PasshareController {
    private final PasshareSessionService sessionService;
    private final StorageService storageService;

    public PasshareController(PasshareSessionService sessionService, StorageService storageService) {
        this.sessionService = sessionService;
        this.storageService = storageService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<Map<String, Object>> createSession(@RequestAttribute("userId") Long userId) {
        try {
            PasshareSession session = sessionService.createSession(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", session.getId());
            response.put("code", session.getCode());
            response.put("creatorId", session.getCreatorId());
            response.put("createdAt", session.getCreatedAt());
            response.put("expiresAt", session.getExpiresAt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace
            Map<String, Object> error = new HashMap<>();
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName() + ": " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown error");
            }
            error.put("error", errorMessage);
            error.put("exception", e.getClass().getName());
            if (e.getCause() != null) {
                error.put("cause", e.getCause().getMessage());
            }
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sessions/join")
    public ResponseEntity<Map<String, Object>> joinSession(
            @RequestAttribute("userId") Long userId,
            @RequestParam String code) {
        try {
            PasshareSession session = sessionService.joinSession(code, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", session.getId());
            response.put("code", session.getCode());
            response.put("creatorId", session.getCreatorId());
            response.put("createdAt", session.getCreatedAt());
            response.put("expiresAt", session.getExpiresAt());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId) {
        Optional<PasshareSession> sessionOpt = sessionService.getSession(sessionId, userId);
        if (sessionOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Session not found or access denied");
            return ResponseEntity.notFound().build();
        }

        PasshareSession session = sessionOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("code", session.getCode());
        response.put("creatorId", session.getCreatorId());
        response.put("createdAt", session.getCreatedAt());
        response.put("expiresAt", session.getExpiresAt());
        response.put("active", session.isActive());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions/{sessionId}/files")
    public ResponseEntity<Map<String, Object>> shareFile(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @RequestParam Long fileId) {
        try {
            PasshareSessionFile sessionFile = sessionService.shareFile(sessionId, fileId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("id", sessionFile.getId());
            response.put("fileId", sessionFile.getFileItem().getId());
            response.put("fileName", sessionFile.getFileItem().getName());
            response.put("sharedByUserId", sessionFile.getSharedByUserId());
            response.put("sharedAt", sessionFile.getSharedAt());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sessions/{sessionId}/files")
    public ResponseEntity<?> getSessionFiles(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId) {
        try {
            List<PasshareSessionFile> files = sessionService.getSessionFiles(sessionId, userId);
            // Convert to DTO to avoid lazy loading issues
            List<Map<String, Object>> fileData = files.stream().map(f -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", f.getId());
                data.put("fileId", f.getFileItem().getId());
                data.put("fileName", f.getFileItem().getName());
                data.put("fileSize", f.getFileItem().getSize());
                data.put("fileMimeType", f.getFileItem().getMimeType());
                data.put("sharedByUserId", f.getSharedByUserId());
                data.put("sharedAt", f.getSharedAt());
                data.put("downloadCount", f.getDownloadCount());
                return data;
            }).toList();
            return ResponseEntity.ok(fileData);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Failed to get session files");
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/sessions/{sessionId}/participants")
    public ResponseEntity<?> getSessionParticipants(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId) {
        try {
            List<PasshareSessionParticipant> participants = sessionService.getSessionParticipants(sessionId, userId);
            // Convert to DTO to avoid lazy loading issues
            List<Map<String, Object>> participantData = participants.stream().map(p -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", p.getId());
                data.put("userId", p.getUserId());
                data.put("joinedAt", p.getJoinedAt());
                return data;
            }).toList();
            return ResponseEntity.ok(participantData);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Failed to get participants");
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sessions/{sessionId}/leave")
    public ResponseEntity<Map<String, Object>> leaveSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId) {
        try {
            sessionService.leaveSession(sessionId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Left session successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sessions/{sessionId}/files/{sessionFileId}/download")
    public ResponseEntity<Resource> downloadSessionFile(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @PathVariable Long sessionFileId) throws IOException {
        try {
            PasshareSessionFile sessionFile = sessionService.getSessionFile(sessionId, sessionFileId, userId);
            FileItem fileItem = sessionFile.getFileItem();
            
            // Increment download count
            sessionService.incrementSessionFileDownloadCount(sessionId, sessionFileId, userId);
            
            // Load and serve the file directly - no ownership check needed for session participants
            byte[] content = storageService.load(fileItem.getStorageKey());
            ByteArrayResource resource = new ByteArrayResource(content);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileItem.getName() + "\"")
                    .contentType(MediaType.parseMediaType(fileItem.getMimeType()))
                    .contentLength(content.length)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/sessions/{sessionId}/files/{sessionFileId}")
    public ResponseEntity<Map<String, Object>> removeSharedFile(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @PathVariable Long sessionFileId) {
        try {
            sessionService.removeSharedFile(sessionId, sessionFileId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File removed from session successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage() != null ? e.getMessage() : "Failed to remove file");
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/sessions/{sessionId}/end")
    public ResponseEntity<Map<String, Object>> endSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId) {
        try {
            sessionService.endSession(sessionId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session ended successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

