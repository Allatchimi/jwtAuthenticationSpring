package com.kidami.security.services;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LessonVideoItemService {

    LessonVideoItemDTO addLessonVideoItem(Integer lessonId, LessonVideoItemReq lessonVideoItemReq);
    List<LessonVideoItemDTO> getAllLessonVideoItem();
    LessonVideoItemDTO updateLessonVideoItem(LessonVideoItemUpdateDTO lessonVideoItemReq);
    // Méthode pour supprimer un cours par son ID
    ResponseEntity<String> deleteLessonVideoItem(Integer id);
    List<LessonVideoItemDTO> getLessonsByLessonId(Integer lessonId);
}
