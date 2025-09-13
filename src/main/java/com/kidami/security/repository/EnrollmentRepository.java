package com.kidami.security.repository;

import com.kidami.security.models.Cour;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    boolean existsByStudentAndCour(User student, Cour cour);
    Optional<Enrollment> findByStudentAndCour(User student, Cour cour);
}