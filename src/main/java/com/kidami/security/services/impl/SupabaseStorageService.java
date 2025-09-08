package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Profile("prod")
public class SupabaseStorageService implements StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.api.key}")
    private String apiKey;

    private final HttpClient httpClient;

    public SupabaseStorageService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        try {
            String fileName = generateSecureFileName(file.getOriginalFilename());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/storage/v1/object/%s/%s",
                            supabaseUrl.trim(), bucket.trim(), fileName)))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Content-Type", file.getContentType())
                    .header("Cache-Control", "max-age=31536000")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .timeout(Duration.ofSeconds(45))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return String.format("%s/storage/v1/object/public/%s/%s",
                        supabaseUrl.trim(), bucket.trim(), fileName);
            } else {
                throw new IOException(String.format(
                        "Erreur Supabase [%d]: %s",
                        response.statusCode(),
                        response.body()
                ));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Upload interrompu", e);
        } catch (Exception e) {
            throw new IOException("Erreur lors de l'upload vers Supabase", e);
        }
    }

    @Override
    public Resource loadFile(String fileName) throws IOException {
        try {
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s",
                    supabaseUrl.trim(), bucket.trim(), fileName);

            return new UrlResource(URI.create(publicUrl));

        } catch (Exception e) {
            throw new IOException("Erreur lors du chargement du fichier depuis Supabase", e);
        }
    }

    @Override
    public List<String> listFiles() throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/storage/v1/object/list/%s",
                            supabaseUrl.trim(), bucket.trim())))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                // Parsez la réponse JSON pour extraire les noms de fichiers
                // Exemple de réponse: [{"name":"file1.jpg"},{"name":"file2.mp4"}]
                return parseFileNamesFromJson(response.body());
            } else {
                throw new IOException("Erreur liste Supabase: " + response.statusCode());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Liste interrompue", e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/storage/v1/object/%s/%s",
                            supabaseUrl.trim(), bucket.trim(), fileName)))
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .DELETE()
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Suppression interrompue", e);
        }
    }

    private String generateSecureFileName(String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";

        return String.format("%s_%d_%s",
                UUID.randomUUID().toString().substring(0, 8),
                System.currentTimeMillis(),
                cleanName);
    }

    private List<String> parseFileNamesFromJson(String jsonResponse) {
        // Implémentation basique - utilisez Jackson ou Gson pour du vrai parsing
        return List.of(); // À implémenter selon le format de réponse Supabase
    }

    public String getFileUrl(String fileName) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl.trim(), bucket.trim(), fileName);
    }
}