package com.kidami.security.services;

import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.Purchase;
import jakarta.transaction.Transactional;

import java.util.List;

public interface EnrollmentService {

    EnrollementDTO enrollUser(Long userId, Long courseId);
    List<EnrollementDTO> getUserEnrollments(Long userId);
    List<EnrollementDTO> getCourseEnrollments(Long courseId);

    @Transactional
    void enrollFromPurchase(Purchase purchase);
}
