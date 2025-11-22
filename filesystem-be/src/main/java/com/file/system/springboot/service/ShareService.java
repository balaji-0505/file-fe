package com.file.system.springboot.service;

import com.file.system.springboot.model.FileItem;
import com.file.system.springboot.model.ShareLink;
import com.file.system.springboot.model.ShareType;
import com.file.system.springboot.repository.FileItemRepository;
import com.file.system.springboot.repository.ShareLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {
    private final ShareLinkRepository shareLinkRepository;
    private final FileItemRepository fileItemRepository;

    public ShareService(ShareLinkRepository shareLinkRepository, FileItemRepository fileItemRepository) {
        this.shareLinkRepository = shareLinkRepository;
        this.fileItemRepository = fileItemRepository;
    }

    @Transactional
    public ShareLink create(Long fileId, Long ownerId, ShareType type, String permissionsCsv, Instant expiryAt, String passwordHash, String createdBy) {
        FileItem fileItem = fileItemRepository.findByIdAndOwnerId(fileId, ownerId).orElseThrow();
        ShareLink link = new ShareLink();
        link.setFileItem(fileItem);
        link.setShareType(type);
        link.setPermissions(permissionsCsv);
        link.setExpiryAt(expiryAt);
        link.setPasswordHash(passwordHash);
        link.setLinkId(UUID.randomUUID().toString().replace("-", ""));
        link.setCreatedBy(createdBy);
        return shareLinkRepository.save(link);
    }

    public Optional<ShareLink> findByLinkId(String linkId) {
        return shareLinkRepository.findByLinkId(linkId);
    }

    public List<ShareLink> listAll(Long ownerId) {
        // naive filtering by owner via file relation; better to store owner_id on share_links as well
        return shareLinkRepository.findAll().stream()
                .filter(s -> s.getFileItem() != null && ownerId.equals(s.getFileItem().getOwnerId()))
                .toList();
    }

    @Transactional
    public ShareLink update(Long id, Long ownerId, ShareType type, String permissionsCsv, Instant expiryAt, String passwordHash) {
        ShareLink link = shareLinkRepository.findById(id).orElseThrow();
        if (link.getFileItem() == null || !ownerId.equals(link.getFileItem().getOwnerId())) {
            throw new IllegalArgumentException("Not found");
        }
        if (type != null) link.setShareType(type);
        if (permissionsCsv != null) link.setPermissions(permissionsCsv);
        link.setExpiryAt(expiryAt);
        link.setPasswordHash(passwordHash);
        return shareLinkRepository.save(link);
    }

    @Transactional
    public void delete(Long id, Long ownerId) {
        ShareLink link = shareLinkRepository.findById(id).orElseThrow();
        if (link.getFileItem() == null || !ownerId.equals(link.getFileItem().getOwnerId())) {
            throw new IllegalArgumentException("Not found");
        }
        shareLinkRepository.delete(link);
    }
}


