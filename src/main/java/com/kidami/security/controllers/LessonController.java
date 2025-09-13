package com.kidami.security.controllers;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.lessonDTO.LessonDTO;
import com.kidami.security.dto.lessonDTO.LessonDelete;
import com.kidami.security.dto.lessonDTO.LessonSaveDTO;
import com.kidami.security.dto.lessonDTO.LessonUpdateDTO;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.LessonService;
import com.kidami.security.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            @Parameter(
                    description = "Données du Lesson",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonSaveDTO.class))
            )
            @RequestPart("lessonSaveDTO") LessonSaveDTO lessonSaveDTO,
            @Parameter(description = "Fichier thumbnail du Lesson",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ){
        log.info("addLesson a add lesson");
         LessonDTO lessonDTO = lessonService.addLesson(lessonSaveDTO, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.created("created lesson succes", lessonDTO,null));
    }

    @GetMapping("/getAllLessons")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> getAllLessons(){
        List<LessonDTO> lessonDTOList = lessonService.getAllLesson();
        if(lessonDTOList.isEmpty()){
            return ResponseEntity.ok(
                    ResponseUtil.error("No lessons found", Collections.emptyList(), null));
        }
        return ResponseEntity.ok(ResponseUtil.success("retrived all lessons",lessonDTOList,null));

    }

    @GetMapping("/byCourse/{courId}")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> getLessonsByCourseId(@PathVariable Long courId) {
       List<LessonDTO>  lessonDTOList = lessonService.getLessonsByCourId(courId);
        return ResponseEntity.ok(ResponseUtil.success("retrived lesson",lessonDTOList,null));
    }

    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse<LessonDTO>> getLessonByName(@RequestParam String name) {
        LessonDTO lessonDTO = lessonService.getLessonByName(name);
        return ResponseEntity.ok(ResponseUtil.success("retrived lesson",lessonDTO,null));
    }

    @PutMapping("/updateLesson")
    public  ResponseEntity<ApiResponse<LessonDTO>> updateLesson(@RequestBody LessonUpdateDTO lessonUpdateDTO){
        LessonDTO lessonDTO = lessonService.updateLesson(lessonUpdateDTO);
        return ResponseEntity.ok().body(ResponseUtil.success("Lesson updated successfully",lessonDTO,null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDelete>> deleteLesson(@PathVariable(value="id") Long id){
        try {
            LessonDelete deleteResponse = lessonService.deleteLesson(id);
            return ResponseEntity.ok(ResponseUtil.success("Lesson deleted successfully", deleteResponse, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Lesson not found", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUtil.error("Error deleting lesson", null, null));
        }
    }
}
