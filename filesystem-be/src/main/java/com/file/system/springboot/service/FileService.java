package com.file.system.springboot.service;

import com.file.system.springboot.model.FileItem;
import com.file.system.springboot.model.Folder;
import com.file.system.springboot.repository.FileItemRepository;
import com.file.system.springboot.repository.FolderRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileItemRepository fileItemRepository;
    private final FolderRepository folderRepository;
    private final StorageService storageService;

    public FileService(FileItemRepository fileItemRepository,
                       FolderRepository folderRepository,
                       StorageService storageService) {
        this.fileItemRepository = fileItemRepository;
        this.folderRepository = folderRepository;
        this.storageService = storageService;
    }

    @Transactional
    public FileItem upload(MultipartFile file, Long folderId, Long ownerId) throws IOException {
        String storageKey = storageService.store(file);
        FileItem item = new FileItem();
        item.setName(file.getOriginalFilename());
        item.setSize(file.getSize());
        item.setMimeType(Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));
        item.setStorageKey(storageKey);
        item.setOwnerId(ownerId);
        if (folderId != null) {
            Folder folder = folderRepository.findById(folderId).orElseThrow();
            item.setFolder(folder);
        }
        return fileItemRepository.save(item);
    }

    public List<FileItem> listAll(Long ownerId) {
        return fileItemRepository.findAllByOwnerId(ownerId);
    }

    public Optional<FileItem> getById(Long id, Long ownerId) {
        return fileItemRepository.findByIdAndOwnerId(id, ownerId);
    }

    @Transactional
    public FileItem update(Long id, Long ownerId, String name, Boolean starred) {
        FileItem item = fileItemRepository.findByIdAndOwnerId(id, ownerId).orElseThrow();
        if (name != null) item.setName(name);
        if (starred != null) item.setStarred(starred);
        return fileItemRepository.save(item);
    }

    @Transactional
    public void incrementDownloadCount(Long id, Long ownerId) {
        FileItem item = fileItemRepository.findByIdAndOwnerId(id, ownerId).orElseThrow();
        item.setDownloadCount(item.getDownloadCount() + 1);
        fileItemRepository.save(item);
    }

    @Transactional
    public void delete(Long id, Long ownerId) throws IOException {
        FileItem item = fileItemRepository.findByIdAndOwnerId(id, ownerId).orElseThrow();
        storageService.delete(item.getStorageKey());
        fileItemRepository.delete(item);
    }
}


