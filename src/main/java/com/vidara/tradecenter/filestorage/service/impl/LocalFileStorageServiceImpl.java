package com.vidara.tradecenter.filestorage.service.impl;

import com.vidara.tradecenter.common.constants.AppConstants;
import com.vidara.tradecenter.common.exception.BadRequestException;
import com.vidara.tradecenter.filestorage.dto.response.FileResponse;
import com.vidara.tradecenter.filestorage.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private final Path uploadRootPath;

    public LocalFileStorageServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadRootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public FileResponse uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        validateFileType(file);
        validateFileSize(file);

        String safeSubDir = (subDir == null || subDir.isBlank()) ? "misc" : subDir;
        Path targetDir = uploadRootPath.resolve(safeSubDir).normalize();

        try {
            Files.createDirectories(targetDir);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null
                    ? "file"
                    : file.getOriginalFilename());
            String ext = getExtension(originalName);
            String storedName = UUID.randomUUID() + ext;
            Path targetPath = targetDir.resolve(storedName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + safeSubDir + "/" + storedName;
            return new FileResponse(storedName, fileUrl, file.getContentType(), file.getSize());
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FileResponse> uploadMultipleFiles(MultipartFile[] files, String subDir) {
        if (files == null || files.length == 0) {
            throw new BadRequestException("No files provided");
        }
        return Arrays.stream(files)
                .map(file -> uploadFile(file, subDir))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new BadRequestException("File path is required");
        }

        String normalized = filePath.replace("\\", "/");
        if (normalized.startsWith("/uploads/")) {
            normalized = normalized.substring("/uploads/".length());
        }

        Path absolutePath = uploadRootPath.resolve(normalized).normalize();
        if (!absolutePath.startsWith(uploadRootPath)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            Files.deleteIfExists(absolutePath);
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || Arrays.stream(AppConstants.ALLOWED_IMAGE_TYPES)
                .noneMatch(allowed -> allowed.equalsIgnoreCase(contentType))) {
            throw new BadRequestException("Unsupported file type: " + contentType);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > AppConstants.MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size of 5MB");
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }
}
