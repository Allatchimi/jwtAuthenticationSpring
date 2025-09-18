package com.kidami.security.services.impl;

import com.kidami.security.services.StorageService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("prod")
public class SupabaseStorageService implements StorageService {

    private final String supabaseUrl;
    private final String bucket;
    private final String apiKey;
    private final WebClient.Builder webClientBuilder;
    private final WebClient webClient;

    public SupabaseStorageService(WebClient.Builder webClientBuilder) {
        Dotenv dotenv = Dotenv.load();
        this.supabaseUrl = dotenv.get("SUPABASE_URL");
        this.bucket = dotenv.get("SUPABASE_BUCKET");
        this.apiKey = dotenv.get("SUPABASE_API_KEY");
        this.webClientBuilder = webClientBuilder;
        this.webClient = webClientBuilder
                .baseUrl(supabaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    // Méthode générale pour sauvegarder un fichier
    @Override
    public String saveFile(MultipartFile file, String fileType, String subfolder) throws IOException {
        try {
            String fileName = generateSupabaseFilePath(fileType, subfolder, file.getOriginalFilename());

            String res = webClient.post()
                    .uri("/storage/v1/object/{bucket}/{fileName}", bucket, fileName)
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header("Cache-Control", "max-age=31536000")
                    .bodyValue(file.getBytes())
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur Supabase: " + response.statusCode())))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(45))
                    .block();

            return fileName; // Retourne le chemin complet avec structure

        } catch (Exception e) {
            throw new IOException("Erreur lors de l'upload vers Supabase", e);
        }
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

    // Méthodes de base (restent inchangées)
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
        try {
            webClient.delete()
                    .uri("/storage/v1/object/{bucket}/{fileName}", bucket, fileName)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur suppression Supabase: " + response.statusCode())))
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(30))
                    .block();

            return true;
        } catch (Exception e) {
            throw new IOException("Erreur lors de la suppression du fichier", e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                supabaseUrl.trim(), bucket.trim(), fileName);
    }

    // Méthode pour lister les fichiers avec structure
    @Override
    public List<String> listFiles(String fileType, String subfolder) throws IOException {
        try {
            String prefix = getSupabasePrefix(fileType, subfolder);

            // Supabase retourne une liste d'objets avec des metadata
            List<Map<String, Object>> objects = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/storage/v1/object/list/{bucket}")
                            .queryParam("prefix", prefix)
                            .build(bucket))
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> Mono.error(new IOException("Erreur liste Supabase: " + response.statusCode())))
                    .bodyToMono(List.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            // Extraire juste les noms de fichiers
            return objects.stream()
                    .map(obj -> (String) obj.get("name"))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new IOException("Erreur lors du listing des fichiers", e);
        }
    }

    // Méthodes utilitaires pour Supabase
    private String generateSupabaseFilePath(String fileType, String subfolder, String originalFileName) {
        String cleanName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_") : "file";

        String fileName = String.format("%s_%d_%s",
                UUID.randomUUID().toString().substring(0, 8),
                System.currentTimeMillis(),
                cleanName);

        // Créer le chemin structuré pour Supabase
        switch (fileType.toLowerCase()) {
            case "image":
                return String.format("images/%s/%s", subfolder, fileName);
            case "video":
                return String.format("videos/%s/%s", subfolder, fileName);
            case "document":
                return String.format("documents/%s/%s", subfolder, fileName);
            default:
                return String.format("other/%s/%s", subfolder, fileName);
        }
    }

    private String getSupabasePrefix(String fileType, String subfolder) {
        switch (fileType.toLowerCase()) {
            case "image":
                return String.format("images/%s/", subfolder);
            case "video":
                return String.format("videos/%s/", subfolder);
            case "document":
                return String.format("documents/%s/", subfolder);
            default:
                return String.format("other/%s/", subfolder);
        }
    }
}