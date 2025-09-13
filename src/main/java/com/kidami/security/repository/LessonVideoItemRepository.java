package com.kidami.security.repository;

import com.kidami.security.models.LessonVideoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LessonVideoItemRepository extends JpaRepository<LessonVideoItem,Long> {

    List<LessonVideoItem> findByLessonId(Long lesson_id);
    // Méthode pour suppression multiple
    void deleteAllByIdIn(List<Long> ids);
    Optional<LessonVideoItem> findByName(String name);
    Optional<LessonVideoItem> findById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM LessonVideoItem c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
}
