package com.file.system.springboot.controller;

import com.file.system.springboot.model.ShareLink;
import com.file.system.springboot.model.ShareType;
import com.file.system.springboot.service.ShareService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "*")
public class ShareController {
    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping
    public List<ShareLink> list(@RequestAttribute("userId") Long userId) { return shareService.listAll(userId); }

    @PostMapping
    public ShareLink create(@RequestAttribute("userId") Long userId,
                            @RequestParam Long fileId,
                            @RequestParam ShareType shareType,
                            @RequestParam(required = false) String permissions,
                            @RequestParam(required = false) Long expiryEpochMs,
                            @RequestParam(required = false) String password,
                            @RequestParam(required = false) String createdBy) {
        Instant expiry = expiryEpochMs != null ? Instant.ofEpochMilli(expiryEpochMs) : null;
        String passwordHash = password; // For demo only; hash in production
        return shareService.create(fileId, userId, shareType, permissions, expiry, passwordHash, createdBy);
    }

    @PatchMapping("/{id}")
    public ShareLink update(@RequestAttribute("userId") Long userId,
                            @PathVariable Long id,
                            @RequestParam(required = false) ShareType shareType,
                            @RequestParam(required = false) String permissions,
                            @RequestParam(required = false) Long expiryEpochMs,
                            @RequestParam(required = false) String password) {
        Instant expiry = expiryEpochMs != null ? Instant.ofEpochMilli(expiryEpochMs) : null;
        String passwordHash = password;
        return shareService.update(id, userId, shareType, permissions, expiry, passwordHash);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestAttribute("userId") Long userId,
                       @PathVariable Long id) { shareService.delete(id, userId); }
}


