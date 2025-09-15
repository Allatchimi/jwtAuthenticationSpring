package com.kidami.security.exceptions;

import com.kidami.security.responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Méthode utilitaire pour vérifier si c'est une requête média
    private boolean isMediaRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path != null && (path.contains("/api/images/") || path.contains("/api/videos/"));
    }

    // Gestion des ressources non trouvées
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException e, WebRequest request) {
        // Ignorer les requêtes média
        if (isMediaRequest(request)) {
            return null;
        }

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                e.getMessage(),
                null,
                createErrorDetails(request, HttpStatus.NOT_FOUND)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Gestion des validations échouées (DTO validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        // Ignorer les requêtes média
        if (isMediaRequest(request)) {
            return null;
        }

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                "Validation failed",
                errors,
                createErrorDetails(request, HttpStatus.BAD_REQUEST)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Gestion des exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        // Ignorer les requêtes média - IMPORTANT pour résoudre le problème
        if (isMediaRequest(request)) {
            return null;
        }

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                ex.getMessage(),
                null,
                createErrorDetails(request, HttpStatus.INTERNAL_SERVER_ERROR)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Gestion des violations de contraintes d'unicité (ex: email déjà existant)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {

        // Ignorer les requêtes média
        if (isMediaRequest(request)) {
            return null;
        }

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                ex.getMessage(),
                null,
                createErrorDetails(request, HttpStatus.CONFLICT)
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Validation JPA des entités (ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        // Ignorer les requêtes média
        if (isMediaRequest(request)) {
            return null;
        }

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        List<String> errors = violations.stream()
                .map(violation ->
                        String.format("%s: %s",
                                violation.getPropertyPath(),
                                violation.getMessage()))
                .collect(Collectors.toList());

        ApiResponse<Object> response = new ApiResponse<>(
                "error",
                "Data validation failed",
                errors,
                createErrorDetails(request, HttpStatus.BAD_REQUEST)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Méthode utilitaire pour créer les détails d'erreur
    private Map<String, Object> createErrorDetails(WebRequest request, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return errorDetails;
    }
}