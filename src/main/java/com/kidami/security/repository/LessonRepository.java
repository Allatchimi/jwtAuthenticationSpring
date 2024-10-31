package com.kidami.security.repository;

import com.kidami.security.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson,Integer> {
    Optional<Lesson> findByName(String name);
}
