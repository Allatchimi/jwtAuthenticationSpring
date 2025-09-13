package com.kidami.security.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    // Méthodes générales
    String saveFile(MultipartFile file, String fileType, String subfolder) throws IOException;
    Resource loadFile(String fileName) throws IOException;
    List<String> listFiles(String fileType, String subfolder) throws IOException;
    boolean deleteFile(String fileName) throws IOException;
    String getFileUrl(String fileName);

    // Méthodes spécifiques pour les images
    String saveImage(MultipartFile file, String subfolder) throws IOException;
    List<String> listImages(String subfolder) throws IOException;

    // Méthodes spécifiques pour les vidéos
    String saveVideo(MultipartFile file, String subfolder) throws IOException;
    List<String> listVideos(String subfolder) throws IOException;

    // Méthodes spécifiques pour les documents
    String saveDocument(MultipartFile file, String subfolder) throws IOException;
    List<String> listDocuments(String subfolder) throws IOException;
}