package com.kidami.security.services;

import com.kidami.security.dto.lessonDTO.LessonDTO;
import com.kidami.security.dto.lessonDTO.LessonDelete;
import com.kidami.security.dto.lessonDTO.LessonSaveDTO;
import com.kidami.security.dto.lessonDTO.LessonUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonService {

    LessonDTO addLesson(LessonSaveDTO lessonSaveDTO, MultipartFile imageFile);
    LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO);
    List<LessonDTO> getAllLesson();
    LessonDelete deleteLesson(Long id);
    LessonDTO getLessonByName(String name);
    LessonDTO getLessonById(Long id);
    List<LessonDTO> getLessonsByCourId(Long courId);

}
