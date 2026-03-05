package com.vidara.tradecenter.filestorage.service;

import com.vidara.tradecenter.filestorage.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    FileResponse uploadFile(MultipartFile file, String subDir);

    List<FileResponse> uploadMultipleFiles(MultipartFile[] files, String subDir);

    void deleteFile(String filePath);
}
