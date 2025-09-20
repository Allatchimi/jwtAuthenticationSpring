package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import jakarta.transaction.Transactional;
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
import java.util.stream.Stream;

@Service
@Profile("dev")
@Transactional
public class LocalStorageService implements StorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Méthodes générales (déjà existantes)
    @Override
    public String saveFile(MultipartFile file, String fileType, String subfolder) throws IOException {
        Path targetPath = determineTargetPath(fileType, subfolder);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        String fileName = generateSecureFileName(file.getOriginalFilename());
        Path filePath = targetPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return getRelativePath(fileType, subfolder, fileName);
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
    public List<String> listFiles(String fileType, String subfolder) throws IOException {
        Path targetPath = determineTargetPath(fileType, subfolder);
        if (!Files.exists(targetPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.list(targetPath)) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
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
        return fileName;
    }

    // Méthodes spécifiques pour les IMAGES
    @Override
    public String saveImage(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "image", subfolder);
    }

    @Override
    public List<String> listImages(String subfolder) throws IOException {
        return listFiles("image", subfolder);
    }

    // Méthodes spécifiques pour les VIDEOS
    @Override
    public String saveVideo(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "video", subfolder);
    }

    @Override
    public List<String> listVideos(String subfolder) throws IOException {
        return listFiles("video", subfolder);
    }

    // Méthodes spécifiques pour les DOCUMENTS
    @Override
    public String saveDocument(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "document", subfolder);
    }

    @Override
    public List<String> listDocuments(String subfolder) throws IOException {
        return listFiles("document", subfolder);
    }

    // Méthodes utilitaires privées
    private Path determineTargetPath(String fileType, String subfolder) {
        switch (fileType.toLowerCase()) {
            case "image": return Paths.get(uploadDir, "images", subfolder);
            case "video": return Paths.get(uploadDir, "videos", subfolder);
            case "document": return Paths.get(uploadDir, "documents", subfolder);
            default: return Paths.get(uploadDir, "other", subfolder);
        }
    }

    private String getRelativePath(String fileType, String subfolder, String fileName) {
        switch (fileType.toLowerCase()) {
            case "image": return Paths.get("images", subfolder, fileName).toString();
            case "video": return Paths.get("videos", subfolder, fileName).toString();
            case "document": return Paths.get("documents", subfolder, fileName).toString();
            default: return Paths.get("other", subfolder, fileName).toString();
        }
    }

    private String generateSecureFileName(String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";
        return String.format("%d_%s", System.currentTimeMillis(), cleanName);
    }
}