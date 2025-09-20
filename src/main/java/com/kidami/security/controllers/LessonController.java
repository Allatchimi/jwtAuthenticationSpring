package com.kidami.security.controllers;

import com.kidami.security.dto.lessonDTO.*;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.LessonService;
import com.kidami.security.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping(value = "/addLesson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonDTO>> addLesson(
            @RequestPart("lessonSaveDTO") @Valid LessonSaveDTO lessonSaveDTO,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles
    ) {
        log.info("Ajout d'une leçon");
        LessonDTO lessonDTO = lessonService.addLesson(lessonSaveDTO, imageFile, videoFiles);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.created("Lesson créée avec succès", lessonDTO, null));
    }

    @GetMapping("/getAllLessons")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> getAllLessons() {
        List<LessonDTO> lessonDTOList = lessonService.getAllLesson();
        if (lessonDTOList.isEmpty()) {
            return ResponseEntity.ok(ResponseUtil.error("Aucune leçon trouvée", Collections.emptyList(), null));
        }
        return ResponseEntity.ok(ResponseUtil.success("Toutes les leçons récupérées", lessonDTOList, null));
    }

    @GetMapping("/byCourse/{courId}")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> getLessonsByCourseId(@PathVariable Long courId) {
        List<LessonDTO> lessonDTOList = lessonService.getLessonsByCourId(courId);
        return ResponseEntity.ok(ResponseUtil.success("Leçons récupérées", lessonDTOList, null));
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<LessonDTO>> getLessonByName(@RequestParam String name) {
        LessonDTO lessonDTO = lessonService.getLessonByName(name);
        return ResponseEntity.ok(ResponseUtil.success("Leçon récupérée", lessonDTO, null));
    }

    @PutMapping("/updateLesson")
    public ResponseEntity<ApiResponse<LessonDTO>> updateLesson(@RequestBody LessonUpdateDTO lessonUpdateDTO) {
        LessonDTO lessonDTO = lessonService.updateLesson(lessonUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("Leçon mise à jour avec succès", lessonDTO, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDelete>> deleteLesson(@PathVariable Long id) {
        try {
            LessonDelete deleteResponse = lessonService.deleteLesson(id);
            return ResponseEntity.ok(ResponseUtil.success("Leçon supprimée avec succès", deleteResponse, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Leçon non trouvée", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.error("Erreur lors de la suppression de la leçon", null, null));
        }
    }
}
