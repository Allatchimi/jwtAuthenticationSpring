package com.kidami.security.exceptions;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.kidami.security.controllers")
public class MediaExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleMediaException(Exception ex, WebRequest request) {
        // Ignorer les ClientAbortException (broken pipe)
        if (ex instanceof ClientAbortException ||
                (ex.getCause() != null && ex.getCause() instanceof ClientAbortException)) {
            // Just log and return null - client a déjà fermé la connexion
            logClientAbort(ex);
            return null;
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", new Date());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Media processing error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", getPathFromRequest(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUploadException(FileUploadException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", new Date());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "File upload error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("path", getPathFromRequest(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    private String getPathFromRequest(WebRequest request) {
        String description = request.getDescription(false);
        return description != null ? description.replace("uri=", "") : "unknown";
    }

    private void logClientAbort(Exception ex) {
        // Log léger pour les ClientAbortException (optionnel)
        System.out.println("Client aborted connection: " + ex.getMessage());
    }
}