package com.file.system.springboot.controller;

import com.file.system.springboot.model.FileItem;
import com.file.system.springboot.service.FileService;
import com.file.system.springboot.service.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;
    private final StorageService storageService;

    public FileController(FileService fileService, StorageService storageService) {
        this.fileService = fileService;
        this.storageService = storageService;
    }

    @GetMapping
    public List<FileItem> list(@RequestAttribute("userId") Long userId) {
        return fileService.listAll(userId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileItem upload(@RequestAttribute("userId") Long userId,
                           @RequestPart("file") MultipartFile file,
                           @RequestParam(value = "folderId", required = false) Long folderId) throws IOException {
        return fileService.upload(file, folderId, userId);
    }

    @PatchMapping("/{id}")
    public FileItem update(@RequestAttribute("userId") Long userId,
                           @PathVariable Long id,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) Boolean starred) {
        return fileService.update(id, userId, name, starred);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestAttribute("userId") Long userId,
                       @PathVariable Long id) throws IOException {
        fileService.delete(id, userId);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@RequestAttribute("userId") Long userId,
                                             @PathVariable Long id) throws IOException {
        FileItem item = fileService.getById(id, userId).orElseThrow();
        byte[] content = storageService.load(item.getStorageKey());
        ByteArrayResource resource = new ByteArrayResource(content);
        fileService.incrementDownloadCount(id, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + item.getName() + "\"")
                .contentType(MediaType.parseMediaType(item.getMimeType()))
                .contentLength(content.length)
                .body(resource);
    }
}


