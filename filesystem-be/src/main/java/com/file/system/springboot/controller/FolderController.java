package com.file.system.springboot.controller;

import com.file.system.springboot.model.Folder;
import com.file.system.springboot.service.FolderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@CrossOrigin(origins = "*")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public List<Folder> list(@RequestAttribute("userId") Long userId) { return folderService.listAll(userId); }

    @PostMapping
    public Folder create(@RequestAttribute("userId") Long userId,
                         @RequestParam String name,
                         @RequestParam(required = false) Long parentId) {
        return folderService.create(name, parentId, userId);
    }

    @PatchMapping("/{id}")
    public Folder rename(@RequestAttribute("userId") Long userId,
                         @PathVariable Long id, @RequestParam String name) {
        return folderService.rename(id, userId, name);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestAttribute("userId") Long userId,
                       @PathVariable Long id) {
        folderService.delete(id, userId);
    }
}


