package com.kidami.security.controllers;

import com.kidami.security.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subfolder", defaultValue = "general") String subfolder) {

        try {
            String filePath = storageService.saveDocument(file, subfolder);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Document uploaded successfully");
            response.put("filePath", filePath);
            response.put("fileName", file.getOriginalFilename());
            response.put("url", storageService.getFileUrl(filePath));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not upload document: " + e.getMessage()));
        }
    }

    @GetMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> getDocument(
            @PathVariable String subfolder,
            @PathVariable String filename) {

        try {
            String fullPath = "documents/" + subfolder + "/" + filename;
            Resource document = storageService.loadFile(fullPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(document);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}