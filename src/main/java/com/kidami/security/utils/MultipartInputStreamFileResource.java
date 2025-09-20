package com.kidami.security.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;
    private final long contentLength;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename, long contentLength) {
        super(inputStream);
        this.filename = filename;
        this.contentLength = contentLength;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return this.contentLength;
    }

    @Service
    public static class SupabaseStorageService {

        private final String SUPABASE_URL = "https://icgyxciqajkwsyjtoiib.supabase.co";
        private final String BUCKET = "folders";
        private final String SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImljZ3l4Y2lxYWprd3N5anRvaWliIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1NzI4OTkzOCwiZXhwIjoyMDcyODY1OTM4fQ.gbzagQtVtbglhACVrEqWeV0NrHA927m81eGbDdqijZo"; // Mettre la clé ici

        private final RestTemplate restTemplate = new RestTemplate();

        public Map<String, Object> upload(MultipartFile file, String subfolder) throws Exception {
            String url = SUPABASE_URL + "/storage/v1/object/folders/" + subfolder + "/" + file.getOriginalFilename();

            // Création du body multipart
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setBearerAuth(SERVICE_ROLE_KEY); // Authorization: Bearer <service_role>
            headers.set("apikey", SERVICE_ROLE_KEY); // Header apikey

            // Multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename(), file.getSize()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur d'écriture du fichier: " + response.getStatusCode());
            }

            return response.getBody();
        }
    }
}
