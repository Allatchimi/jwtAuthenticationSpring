package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("dev")
public class LocalStorageService implements StorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = generateSecureFileName(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/api/videos/" + fileName;
    }

    @Override
    public Resource loadFile(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        if (!filePath.startsWith(Paths.get(uploadDir).normalize())) {
            throw new IOException("Chemin non autorisé");
        }
        return new UrlResource(filePath.toUri());
    }

    @Override
    public List<String> listFiles() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            return Collections.emptyList();
        }

        return Files.list(uploadPath)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        if (!filePath.startsWith(Paths.get(uploadDir).normalize())) {
            throw new IOException("Chemin non autorisé");
        }
        return Files.deleteIfExists(filePath);
    }

    @Override
    public String getFileUrl(String fileName) {
        return "/api/files/" + fileName;
    }
    private String generateSecureFileName(String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";

        return String.format("%d_%s",
                System.currentTimeMillis(),
                cleanName);
    }
}
