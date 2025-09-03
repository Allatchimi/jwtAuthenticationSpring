package com.kidami.security.services;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemSaveDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LessonVideoItemService {

    LessonVideoItemDTO addLessonVideoItem(Integer lessonId, LessonVideoItemSaveDTO lessonVideoItemSaveDTO);
    List<LessonVideoItemDTO> getAllLessonVideoItem();
    LessonVideoItemDTO updateLessonVideoItem(LessonVideoItemUpdateDTO lessonVideoItemReq);
    // Méthode pour supprimer un cours par son ID
    boolean deleteLessonVideoItem(Integer id);
    List<LessonVideoItemDTO> getVideoItemByLessonId(Integer lessonId);
}
