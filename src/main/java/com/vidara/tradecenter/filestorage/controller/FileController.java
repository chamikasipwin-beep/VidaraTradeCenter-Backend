package com.vidara.tradecenter.filestorage.controller;

import com.vidara.tradecenter.filestorage.dto.response.FileResponse;
import com.vidara.tradecenter.filestorage.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "misc") String subDir) {
        return ResponseEntity.ok(fileStorageService.uploadFile(file, subDir));
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<FileResponse>> uploadMultiple(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(defaultValue = "misc") String subDir) {
        return ResponseEntity.ok(fileStorageService.uploadMultipleFiles(files, subDir));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@RequestParam String filePath) {
        fileStorageService.deleteFile(filePath);
        return ResponseEntity.noContent().build();
    }
}
