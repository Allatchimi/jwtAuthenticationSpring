package com.kidami.security.services;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;
import com.kidami.security.dto.LessonDTO;
import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LessonVideoItemService {

   // LessonVideoItemRep addLessonVideoItem(LessonVideoItemReq lessonVideoItemReq);

    LessonVideoItemRep addLessonVideoItem(Integer lessonId, LessonVideoItemReq lessonVideoItemReq);

    List<LessonVideoItemRep> getAllLessonVideoItem();
    LessonVideoItemRep updateLessonVideoItem(LessonVideoItemReq lessonVideoItemReq);

    // Méthode pour supprimer un cours par son ID
    ResponseEntity<String> deleteLessonVideoItem(Integer id);
    List<LessonVideoItemRep> getLessonsByLessonId(Integer lessonId);

}
