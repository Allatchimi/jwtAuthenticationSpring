package com.kidami.security.repository;

import com.kidami.security.models.Enrollment;
import com.kidami.security.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByEnrollment(Enrollment enrollment);
}