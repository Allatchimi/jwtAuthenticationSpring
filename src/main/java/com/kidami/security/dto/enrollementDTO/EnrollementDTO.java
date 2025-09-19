package com.kidami.security.dto.enrollementDTO;

import com.kidami.security.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollementDTO {
    private Long id;
    private String studentName;
    private String courName;
    private LocalDateTime enrollmentDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PENDING, COMPLETED, FAILED
    private String transactionId;
    private BigDecimal amountPaid;
}
