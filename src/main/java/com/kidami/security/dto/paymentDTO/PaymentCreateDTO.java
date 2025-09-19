package com.kidami.security.dto.paymentDTO;

import com.kidami.security.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateDTO {
    private Long purchaseId; // Indispensable
    private BigDecimal amount;
    private PaymentMethod method;
    private String transactionId;
    private boolean success;
}
