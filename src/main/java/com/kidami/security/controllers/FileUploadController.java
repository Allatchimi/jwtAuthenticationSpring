package com.kidami.security.controllers;

import com.kidami.security.services.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Uploader un fichier", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Upload attempt by user: {}", userDetails.getUsername());

        try {
            String fileUrl = storageService.saveFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Fichier uploadé avec succès");
            response.put("url", fileUrl);
            response.put("filename", extractFileNameFromUrl(fileUrl));

            logger.info("File uploaded successfully: {}", fileUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Upload error: {}", e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Échec de l'upload: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @Operation(summary = "Télécharger un fichier")
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadFile(filename);
            String contentType = determineContentType(filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + filename + "\"")
                    .body(file);

        } catch (IOException e) {
            logger.error("File not found: {}", filename);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Lister les fichiers", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> listFiles() {
        try {
            return ResponseEntity.ok(storageService.listFiles());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors du listing des fichiers: " + e.getMessage());
        }
    }

    @Operation(summary = "Supprimer un fichier", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            boolean deleted = storageService.deleteFile(filename);
            if (deleted) {
                return ResponseEntity.ok().body("Fichier supprimé avec succès");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".mp4")) return "video/mp4";
        if (filename.endsWith(".mov")) return "video/quicktime";
        if (filename.endsWith(".avi")) return "video/x-msvideo";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}