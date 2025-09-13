package com.kidami.security.controllers;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.CourService;
import com.kidami.security.services.StorageService;
import com.kidami.security.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/cours")
public class CourController {

    private final CourService courService;
    private final StorageService storageService;
    private final UserRepository userRepository;

    public CourController(CourService courService, StorageService storageService, UserRepository userRepository) {
        this.courService = courService;
        this.storageService = storageService;
        this.userRepository = userRepository;
    }


    @Operation(summary = "Uploader cour avec un fichier", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CourDTO>> saveCour(
            @Parameter(
                    description = "Données du cours",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourSaveDTO.class))
            )
            @Valid @RequestPart("courSaveDTO") CourSaveDTO courSaveDTO,
            @Parameter(description = "Fichier thumbnail du cours",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") MultipartFile file,
            Authentication authentication){
        log.info("Content-Type de la requête: {}", courSaveDTO);
        log.info("Fichier reçu: name={}, size={}, content-type={}",
                file.getName(), file.getSize(), file.getContentType());
        String teacherName = authentication.getName();
        log.info("user added teacher name: " + teacherName);
        CourDTO courDTO = courService.addCour(courSaveDTO,teacherName ,file);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success("Course added successfully",courDTO,null));
    }

    @GetMapping("/getAllCours")
    public ResponseEntity<ApiResponse<List<CourDTO>>> getAllCours() {
        List<CourDTO> allCours = courService.getAllCours();
        if (allCours.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ResponseUtil.success("No courses found", Collections.emptyList(), null));
        }
        return ResponseEntity.ok()
                .body(ResponseUtil.success("Courses retrieved successfully", allCours, null));
    }

    @GetMapping("/courtDetails/{id}")
    ResponseEntity<ApiResponse<CourDeteailDTO>> getCour(@PathVariable(value="id") Integer courId) {
        CourDeteailDTO cour = courService.courtDetails(courId);
        return ResponseEntity.ok(ResponseUtil.success("Course retrieved successfully",cour,null));
    }

    @PutMapping("/updateCour")
    public  ResponseEntity<ApiResponse<CourDTO>> updateCour(@RequestBody CourUpdateDTO courUpdateDTO){
        CourDTO courDTO = courService.updateCour(courUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("Course updated successfully",courDTO,null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCour(@PathVariable("id") Long id) {
        boolean isDeleted = courService.deleteCour(id);
        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtil.success("Course deleted successfully", null, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Course not found", null, null));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Cour>> getPopularCourses() {
        return ResponseEntity.ok(courService.getPopularCourses());
    }
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Enrollment> enrollToCourse(@PathVariable Long courseId,
                                                     Principal principal) {
        Enrollment enrollment = courService.enrollToCourse(courseId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<List<Cour>> getMyCourses(Principal principal) {
        return ResponseEntity.ok(courService.getUserCourses(principal.getName()));
    }


}
