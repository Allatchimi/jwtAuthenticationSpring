package com.kidami.security.repository;

import com.kidami.security.models.Lesson;
import com.kidami.security.models.LessonVideoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonVideoItemRepository extends JpaRepository<LessonVideoItem,Integer> {

    List<LessonVideoItem> findByLessonId(Integer lesson_id);
    // Méthode pour suppression multiple
    void deleteAllByIdIn(List<Integer> ids);

}
