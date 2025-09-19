package com.kidami.security.repository;

import com.kidami.security.models.Like;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndCourseId(Long userId, Long courseId);
    List<Like> findByUserId(Long userId, Pageable pageable);
}
