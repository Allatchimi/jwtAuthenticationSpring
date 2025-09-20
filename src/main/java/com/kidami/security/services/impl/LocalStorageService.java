package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@Service
@Profile("dev")
@Transactional
public class LocalStorageService implements StorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        // Crée les dossiers principaux si nécessaire
        Files.createDirectories(Paths.get(uploadDir, "images"));
        Files.createDirectories(Paths.get(uploadDir, "videos"));
        Files.createDirectories(Paths.get(uploadDir, "documents"));
        Files.createDirectories(Paths.get(uploadDir, "other"));
        log.info("Upload directories initialized at: {}", Paths.get(uploadDir).toAbsolutePath());
    }

    // Méthode générale pour enregistrer un fichier
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
    public Resource loadFile(String filePath) throws IOException {
        Path fullPath = Paths.get(uploadDir, filePath).normalize();
        if (!fullPath.startsWith(Paths.get(uploadDir))) {
            throw new IOException("Chemin non autorisé");
        }
        return new UrlResource(fullPath.toUri());
    }

    @Override
    public List<String> listFiles(String fileType, String subfolder) throws IOException {
        Path targetPath = determineTargetPath(fileType, subfolder);
        log.info("Listing files in: {}", targetPath.toAbsolutePath());
        if (!Files.exists(targetPath)) {
            log.warn("Directory does not exist: {}", targetPath.toAbsolutePath());
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
    public boolean deleteFile(String filePath) throws IOException {
        Path fullPath = Paths.get(uploadDir, filePath).normalize();
        if (!fullPath.startsWith(Paths.get(uploadDir))) {
            throw new IOException("Chemin non autorisé");
        }
        return Files.deleteIfExists(fullPath);
    }

    @Override
    public String getFileUrl(String filePath) {
        return filePath;
    }

    // Spécifique pour les images
    @Override
    public String saveImage(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "images", subfolder);
    }

    @Override
    public List<String> listImages(String subfolder) throws IOException {
        return listFiles("images", subfolder);
    }

    // Spécifique pour les vidéos
    @Override
    public String saveVideo(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "videos", subfolder);
    }

    @Override
    public List<String> listVideos(String subfolder) throws IOException {
        return listFiles("videos", subfolder);
    }

    // Spécifique pour les documents
    @Override
    public String saveDocument(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "documents", subfolder);
    }

    @Override
    public List<String> listDocuments(String subfolder) throws IOException {
        return listFiles("documents", subfolder);
    }

    // --- Utilitaires privés ---
    private Path determineTargetPath(String fileType, String subfolder) {
        return Paths.get(uploadDir, fileType, subfolder);
    }

    private String getRelativePath(String fileType, String subfolder, String fileName) {
        return Paths.get(fileType, subfolder, fileName).toString();
    }

    private String generateSecureFileName(String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";
        return System.currentTimeMillis() + "_" + cleanName;
    }
}
