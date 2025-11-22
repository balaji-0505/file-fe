package com.file.system.springboot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path storageRoot;

    public StorageService(@Value("${app.storage.location}") String storageLocation) throws IOException {
        this.storageRoot = Paths.get(storageLocation).toAbsolutePath().normalize();
        Files.createDirectories(this.storageRoot);
    }

    public String store(MultipartFile file) throws IOException {
        String storageKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = storageRoot.resolve(storageKey);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return storageKey;
    }

    public Path resolve(String storageKey) {
        return storageRoot.resolve(storageKey).normalize();
    }

    public byte[] load(String storageKey) throws IOException {
        return Files.readAllBytes(resolve(storageKey));
    }

    public void delete(String storageKey) throws IOException {
        Files.deleteIfExists(resolve(storageKey));
    }

    public void deleteAll() throws IOException {
        FileSystemUtils.deleteRecursively(this.storageRoot);
        Files.createDirectories(this.storageRoot);
    }
}


