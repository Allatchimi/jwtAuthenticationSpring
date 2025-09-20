package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("prod")
@Transactional
public class SupabaseStorageService implements StorageService {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.api.key}")
    private String apiKey;

    private WebClient webClient;

    public SupabaseStorageService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl(supabaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    // Méthode générique pour gérer les appels
    private <T> T handleRequest(Mono<T> requestMono, int timeoutSeconds) throws IOException {
        try {
            return requestMono.timeout(Duration.ofSeconds(timeoutSeconds)).block();
        } catch (Exception e) {
            throw new IOException("Erreur lors de l'appel Supabase", e);
        }
    }

    @Override
    public String saveFile(MultipartFile file, String fileType, String subfolder) throws IOException {
        String fileName = generateSupabaseFilePath(fileType, subfolder, file.getOriginalFilename());

        // Log avant upload
        System.out.println("[SupabaseStorageService] Upload start: " + fileName);

        // Upload
        handleRequest(webClient.post()
                .uri("/storage/v1/object/{bucket}/{fileName}", bucket, fileName)
                .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                .header("Cache-Control", "max-age=31536000")
                .bodyValue(file.getBytes())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> Mono.error(new IOException("Erreur Supabase: " + response.statusCode())))
                .bodyToMono(String.class), 45);

        // Log après upload réussi
        String publicUrl = getFileUrl(fileName);
        System.out.println("[SupabaseStorageService] Upload success: " + fileName);
        System.out.println("[SupabaseStorageService] Public URL: " + publicUrl);

        return fileName;
    }

    @Override
    public String saveImage(MultipartFile file, String subfolder) throws IOException {
        String fileName = saveFile(file, "image", subfolder);
        System.out.println("[SupabaseStorageService] Image saved: " + fileName);
        System.out.println("[SupabaseStorageService] Accessible at: " + getFileUrl(fileName));
        return fileName;
    }


    @Override
    public String saveVideo(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "video", subfolder);
    }

    @Override
    public String saveDocument(MultipartFile file, String subfolder) throws IOException {
        return saveFile(file, "document", subfolder);
    }

    @Override
    public List<String> listFiles(String fileType, String subfolder) throws IOException {
        String prefix = getSupabasePrefix(fileType, subfolder);

        List<Map<String, Object>> objects = handleRequest(
                webClient.post()
                        .uri("/storage/v1/object/list/{bucket}", bucket)
                        .bodyValue(Map.of(
                                "prefix", prefix,
                                "limit", 100,
                                "offset", 0
                        ))
                        .retrieve()
                        .onStatus(status -> status.isError(),
                                response -> Mono.error(new IOException("Erreur liste Supabase: " + response.statusCode())))
                        .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {}),
                30
        );

        return objects.stream()
                .map(obj -> (String) obj.get("name"))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> listImages(String subfolder) throws IOException {
        return listFiles("image", subfolder);
    }

    @Override
    public List<String> listVideos(String subfolder) throws IOException {
        return listFiles("video", subfolder);
    }

    @Override
    public List<String> listDocuments(String subfolder) throws IOException {
        return listFiles("document", subfolder);
    }

    @Override
    public Resource loadFile(String fileName) throws IOException {
        try {
            String publicUrl = getFileUrl(fileName);
            return new UrlResource(URI.create(publicUrl));
        } catch (Exception e) {
            throw new IOException("Erreur lors du chargement du fichier depuis Supabase", e);
        }
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        handleRequest(webClient.post()
                .uri("/storage/v1/object/delete/{bucket}", bucket)
                .bodyValue(List.of(fileName))
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new IOException("Erreur suppression Supabase: " + response.statusCode() + " " + body)))
                .toBodilessEntity(), 30);

        return true;
    }

    @Override
    public String getFileUrl(String fileName) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl.trim(), bucket.trim(), fileName);
    }

    // Méthodes utilitaires
    private String generateSupabaseFilePath(String fileType, String subfolder, String originalFileName) {
        String cleanName = originalFileName != null
                ? originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                : "file";

        String fileName = String.format("%s_%d_%s",
                UUID.randomUUID().toString().substring(0, 8),
                System.currentTimeMillis(),
                cleanName);

        switch (fileType.toLowerCase()) {
            case "image": return String.format("images/%s/%s", subfolder, fileName);
            case "video": return String.format("videos/%s/%s", subfolder, fileName);
            case "document": return String.format("documents/%s/%s", subfolder, fileName);
            default: return String.format("other/%s/%s", subfolder, fileName);
        }
    }

    private String getSupabasePrefix(String fileType, String subfolder) {
        switch (fileType.toLowerCase()) {
            case "image": return String.format("images/%s/", subfolder);
            case "video": return String.format("videos/%s/", subfolder);
            case "document": return String.format("documents/%s/", subfolder);
            default: return String.format("other/%s/", subfolder);
        }
    }
}
