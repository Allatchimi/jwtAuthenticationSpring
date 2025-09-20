package com.kidami.security.services;

import com.kidami.security.dto.lessonDTO.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface LessonService {

    LessonDTO addLesson(LessonSaveDTO lessonSaveDTO, MultipartFile imageFile, List<MultipartFile> videoFiles);

    LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO);

    List<LessonDTO> getAllLesson();

    LessonDTO getLessonById(Long id);

    List<LessonDTO> getLessonsByCourId(Long courId);

    LessonDTO getLessonByName(String name);

    LessonDelete deleteLesson(Long id);
}
