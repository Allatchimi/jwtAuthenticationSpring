package com.kidami.security.controllers;

import com.kidami.security.services.StorageService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private StorageService storageService;

    // Upload d'une vidéo
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subfolder", defaultValue = "general") String subfolder) {

        try {
            if (file.isEmpty()) {
                throw new FileUploadException("Le fichier est vide");
            }

            // Vérifier que c'est bien une vidéo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new FileUploadException("Le fichier doit être une vidéo");
            }

            String filePath = storageService.saveVideo(file, subfolder);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Video uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("url", storageService.getFileUrl(filePath));
            response.put("type", "video");
            response.put("subfolder", subfolder);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload video: " + e.getMessage()));
        }
    }

    // Récupérer une vidéo
    @GetMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> getVideo(
            @PathVariable String subfolder,
            @PathVariable String filename) {

        try {
            String fullPath = "videos/" + subfolder + "/" + filename;
            Resource video = storageService.loadFile(fullPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, getVideoContentType(filename))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(video);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Lister les vidéos d'un sous-dossier
    @GetMapping("/list/{subfolder}")
    public ResponseEntity<List<String>> listVideos(@PathVariable String subfolder) {
        try {
            List<String> videos = storageService.listVideos(subfolder);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer une vidéo
    @DeleteMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable String subfolder,
            @PathVariable String filename) {

        try {
            String fullPath = "videos/" + subfolder + "/" + filename;
            boolean deleted = storageService.deleteFile(fullPath);
            return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getVideoContentType(String filename) {
        if (filename.toLowerCase().endsWith(".mp4")) return "video/mp4";
        if (filename.toLowerCase().endsWith(".avi")) return "video/x-msvideo";
        if (filename.toLowerCase().endsWith(".mov")) return "video/quicktime";
        if (filename.toLowerCase().endsWith(".webm")) return "video/webm";
        return "application/octet-stream";
    }
}