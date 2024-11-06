package com.kidami.security.controllers;


import com.kidami.security.dto.LessonDTO;
import com.kidami.security.dto.LessonSaveDTO;
import com.kidami.security.models.Lesson;
import com.kidami.security.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lesson")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @PostMapping("/addLesson")
    public ResponseEntity<LessonDTO> addLesson(@RequestBody LessonSaveDTO lessonSaveDTO){
         LessonDTO lessonDTO = lessonService.addLesson(lessonSaveDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(lessonDTO);
    }
    @GetMapping("/getAllLessons")
    public ResponseEntity<List<LessonDTO>> getAllLessons(){
        List<LessonDTO> lessonDTOList = lessonService.getAllLesson();
        return ResponseEntity.ok(lessonDTOList);
    }
    @PostMapping("/byCourse/{courId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourseId(@PathVariable Integer courId) {
       List<LessonDTO>  lessonDTOList = lessonService.getLessonsByCourId(courId);
        return ResponseEntity.ok(lessonDTOList);
    }
    @GetMapping("/by-name")
    public ResponseEntity<LessonDTO> getLessonByName(@RequestParam String name) {
        LessonDTO lessonDTO = lessonService.getLessonByName(name);
        return ResponseEntity.ok(lessonDTO);
    }


}
