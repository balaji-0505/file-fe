package com.file.system.springboot.repository;

import com.file.system.springboot.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByParent(Folder parent);
    List<Folder> findAllByOwnerId(Long ownerId);
    Optional<Folder> findByIdAndOwnerId(Long id, Long ownerId);
}


