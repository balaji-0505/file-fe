package com.file.system.springboot.service;

import com.file.system.springboot.model.Folder;
import com.file.system.springboot.repository.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Transactional
    public Folder create(String name, Long parentId, Long ownerId) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwnerId(ownerId);
        if (parentId != null) {
            Folder parent = folderRepository.findById(parentId).orElseThrow();
            folder.setParent(parent);
        }
        return folderRepository.save(folder);
    }

    public List<Folder> listAll(Long ownerId) {
        return folderRepository.findAllByOwnerId(ownerId);
    }

    public Optional<Folder> getById(Long id, Long ownerId) {
        return folderRepository.findByIdAndOwnerId(id, ownerId);
    }

    @Transactional
    public Folder rename(Long id, Long ownerId, String name) {
        Folder folder = folderRepository.findByIdAndOwnerId(id, ownerId).orElseThrow();
        folder.setName(name);
        return folderRepository.save(folder);
    }

    @Transactional
    public void delete(Long id, Long ownerId) {
        Folder folder = folderRepository.findByIdAndOwnerId(id, ownerId).orElseThrow();
        folderRepository.delete(folder);
    }
}


