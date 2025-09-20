package com.kidami.security.controllers;

import com.kidami.security.services.StorageService;
import com.kidami.security.utils.MultipartInputStreamFileResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/images")

public class ImageController {


    private final StorageService storageService;
    private final MultipartInputStreamFileResource.SupabaseStorageService storageService1;

    public ImageController(StorageService storageService, MultipartInputStreamFileResource.SupabaseStorageService storageService1 ) {
        this.storageService = storageService;
        this.storageService1  = storageService1;
    }
    @PostMapping(value = "/uploadFile"  , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("subfolder") String subfolder) {
        try {
            Map<String, Object> result = storageService1.upload(file, subfolder);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Erreur d'écriture du fichier"));
        }
    }

    // Upload d'une image
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subfolder", defaultValue = "general") String subfolder) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le fichier est vide"));
            }

            // Vérifier que c'est bien une image
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le fichier doit être une image"));
            }

            // Vérifier la taille du fichier
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB max
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le fichier ne doit pas dépasser 10MB"));
            }

            String filePath = storageService.saveImage(file, subfolder);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("url", storageService.getFileUrl(filePath));
            response.put("type", "image");
            response.put("subfolder", subfolder);
            response.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (FileUploadException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur d'écriture du fichier"));

        } catch (Exception e) {
            log.error("Erreur lors de l'upload d'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    // Récupérer une image
    @GetMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String subfolder,
            @PathVariable String filename) {

        try {
            String fullPath = "images/" + subfolder + "/" + filename;
            Resource image = storageService.loadFile(fullPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, getImageContentType(filename))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(image);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Lister les images d'un sous-dossier
    @GetMapping("/list/{subfolder}")
    public ResponseEntity<List<String>> listImages(@PathVariable String subfolder) {
        try {
            List<String> images = storageService.listImages(subfolder);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer une image
    @DeleteMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String subfolder,
            @PathVariable String filename) {

        try {
            String fullPath = "images/" + subfolder + "/" + filename;
            boolean deleted = storageService.deleteFile(fullPath);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getImageContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) return "image/png";
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) return "image/jpeg";
        if (filename.toLowerCase().endsWith(".gif")) return "image/gif";
        if (filename.toLowerCase().endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}