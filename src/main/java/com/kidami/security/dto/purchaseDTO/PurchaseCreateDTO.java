package com.kidami.security.dto.purchaseDTO;

import com.kidami.security.models.Cour;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class PurchaseCreateDTO {
    private Long userId;
    private Set<Long> courseIds; // Liste des IDs des cours à acheter
    private BigDecimal amountTotal;
    private String currency;
}