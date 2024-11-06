package com.kidami.security.services;

import com.kidami.security.dto.LessonDTO;
import com.kidami.security.dto.LessonSaveDTO;
import com.kidami.security.dto.LessonUpdateDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface LessonService {

    LessonDTO addLesson(LessonSaveDTO lessonSaveDTO);
    LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO);
    List<LessonDTO> getAllLesson();
    ResponseEntity<String> deleteLesson(Integer id);
   LessonDTO getLessonByName(String name);
    List<LessonDTO> getLessonsByCourId(Integer courId);

}
