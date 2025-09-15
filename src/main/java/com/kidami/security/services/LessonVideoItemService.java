package com.kidami.security.services;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemSaveDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LessonVideoItemService {

    LessonVideoItemDTO addLessonVideoItem(Long lessonId, LessonVideoItemSaveDTO lessonVideoItemSaveDTO, MultipartFile videoFile,MultipartFile imageFile);
    List<LessonVideoItemDTO> getAllLessonVideoItem();
    LessonVideoItemDTO updateLessonVideoItem(LessonVideoItemUpdateDTO lessonVideoItemReq);
    // Méthode pour supprimer un cours par son ID
    boolean deleteLessonVideoItem(Long id);
    List<LessonVideoItemDTO> getVideoItemByLessonId(Long lessonId);
}
