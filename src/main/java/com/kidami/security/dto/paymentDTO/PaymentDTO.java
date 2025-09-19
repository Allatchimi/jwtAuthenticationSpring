package com.kidami.security.dto.paymentDTO;

import com.kidami.security.enums.PaymentMethod;
import com.kidami.security.enums.PaymentStatus;
import com.kidami.security.models.Purchase;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentDate;
    private Long purchaseId; // Référence simple
}
