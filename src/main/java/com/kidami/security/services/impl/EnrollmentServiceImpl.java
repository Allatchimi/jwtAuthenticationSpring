package com.kidami.security.services.impl;

import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.mappers.EnrollementMapper;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.Purchase;
import com.kidami.security.models.User;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.EnrollmentRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.EnrollmentService;
import com.kidami.security.services.PaymentService;
import com.kidami.security.utils.PaymentCompletedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {


    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourRepository courRepository;
    private final PaymentService paymentService;
    private final EnrollementMapper enrollementMapper;


    @Override
    public List<EnrollementDTO> getUserEnrollments(Long userId) {
        List<Enrollment> enrollmentList = enrollmentRepository.findByStudentId(userId);
        return  enrollmentList.stream().map(enrollementMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollementDTO> getCourseEnrollments(Long courseId) {
        List<Enrollment> enrollmentList = enrollmentRepository.findByStudentId(courseId);
        return enrollmentList.stream().map(enrollementMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EnrollementDTO enrollUser(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Cour cour = courRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(user);
        enrollment.setCour(cour);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return enrollementMapper.toDTO(savedEnrollment);
    }


    @Transactional
    @Override
    public void enrollFromPurchase(Purchase purchase) {
        User user = purchase.getBuyer();

        for (Cour course : purchase.getCourses()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(user);
            enrollment.setCour(course);
            enrollment.setEnrolledAt(LocalDateTime.now());

            enrollmentRepository.save(enrollment);
        }
    }

    @EventListener
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        Purchase purchase = event.getPurchase();
        for (Cour course : purchase.getCourses()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(purchase.getBuyer());
            enrollment.setCour(course);
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }
}
