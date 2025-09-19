package com.kidami.security.controllers;

import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.models.Enrollment;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.EnrollmentService;
import com.kidami.security.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<EnrollementDTO>> enrollUser(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        EnrollementDTO enrollment = enrollmentService.enrollUser(userId, courseId);
        return ResponseEntity.ok(ResponseUtil.success("Inscription réussie", enrollment, null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<EnrollementDTO>>> getUserEnrollments(@PathVariable Long userId) {
        List<EnrollementDTO> enrollments = enrollmentService.getUserEnrollments(userId);
        return ResponseEntity.ok(ResponseUtil.success("Inscriptions récupérées", enrollments, null));
    }
}

