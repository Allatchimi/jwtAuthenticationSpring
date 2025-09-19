package com.kidami.security.dto.purchaseDTO;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.enums.PurchaseStatus;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Payment;
import com.kidami.security.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class PurchaseDTO {
    private Long id;
    private String buyerName; // ou String buyerName si tu veux afficher
    private Set<CourDTO> courses; // DTO léger
    private BigDecimal amountTotal;
    private String currency;
    private PurchaseStatus status;
    private LocalDateTime createdAt;
    private List<PaymentDTO> payments;
}

