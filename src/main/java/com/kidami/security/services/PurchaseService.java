package com.kidami.security.services;

import com.kidami.security.dto.purchaseDTO.PurchaseDTO;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Purchase;
import com.kidami.security.models.User;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface PurchaseService {

    // Créer un nouvel achat
    PurchaseDTO createPurchase(User buyer, Set<Cour> courses, BigDecimal totalAmount, String currency);

    // Valider un achat et créer les enrollments
    @Transactional
    PurchaseDTO validatePurchase(Purchase purchase);

    List<PurchaseDTO> getUserPurchases(Long userId);

    @Transactional
    PurchaseDTO initiatePurchase(Long userId, Set<Long> courseIds, String currency);
}
