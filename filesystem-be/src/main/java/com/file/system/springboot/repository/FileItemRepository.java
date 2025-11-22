package com.file.system.springboot.repository;

import com.file.system.springboot.model.FileItem;
import com.file.system.springboot.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    List<FileItem> findByFolder(Folder folder);
    List<FileItem> findAllByOwnerId(Long ownerId);
    Optional<FileItem> findByIdAndOwnerId(Long id, Long ownerId);
}


