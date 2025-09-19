package com.kidami.security.repository;

import com.kidami.security.models.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndCourseId(Long userId, Long courseId);
    List<Favorite> findByUserId(Long userId, Pageable pageable);
}