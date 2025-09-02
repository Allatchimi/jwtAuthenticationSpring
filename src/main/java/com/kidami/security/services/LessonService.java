package com.kidami.security.services;

import com.kidami.security.dto.lessonDTO.LessonDTO;
import com.kidami.security.dto.lessonDTO.LessonDelete;
import com.kidami.security.dto.lessonDTO.LessonSaveDTO;
import com.kidami.security.dto.lessonDTO.LessonUpdateDTO;

import java.util.List;

public interface LessonService {

    LessonDTO addLesson(LessonSaveDTO lessonSaveDTO);
    LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO);
    List<LessonDTO> getAllLesson();
    LessonDelete deleteLesson(Integer id);
    LessonDTO getLessonByName(String name);
    LessonDTO getLessonById(Integer id);
    List<LessonDTO> getLessonsByCourId(Integer courId);

}
