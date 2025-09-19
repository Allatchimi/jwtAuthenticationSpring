package com.kidami.security.controllers;

import com.kidami.security.dto.paymentDTO.PaymentCreateDTO;
import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.dto.purchaseDTO.PurchaseCreateDTO;
import com.kidami.security.dto.purchaseDTO.PurchaseDTO;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Purchase;
import com.kidami.security.models.User;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.PurchaseRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.PaymentService;
import com.kidami.security.services.PurchaseService;
import com.kidami.security.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserRepository userRepository;
    private final CourRepository courseRepository;
    private final PaymentService paymentService;
    private final PurchaseRepository purchaseRepository;




    @PostMapping("/initialisation")
    public ResponseEntity<ApiResponse<PurchaseDTO>> createPurchase(
            @RequestParam Long userId,
            @RequestBody Set<Long> courseIds,
            @RequestParam String currency) {
        PurchaseDTO purchase = purchaseService.initiatePurchase(userId, courseIds, currency);
        return ResponseEntity.ok(ResponseUtil.success("Achat initié", purchase, null));
    }

    // Créer un achat
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PurchaseDTO>> createPurchase(@RequestBody PurchaseCreateDTO request) {
        // ⚠️ Récupération de l’utilisateur (dans un vrai projet, via SecurityContextHolder)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupération des cours par leurs IDs
        Set<Cour> courses = courseRepository.findAllById(request.getCourseIds())
                .stream().collect(Collectors.toSet());

        // Appel du service
        PurchaseDTO purchase = purchaseService.createPurchase(
                user,
                courses,
                request.getAmountTotal(),
                request.getCurrency()
        );

        return ResponseEntity.ok(ResponseUtil.success("Purchase success", purchase, null));
    }

    // Traiter un paiement
    @PostMapping("/{purchaseId}/pay")
    public ResponseEntity<ApiResponse<PaymentDTO>> payForPurchase(
            @PathVariable Long purchaseId,
            @RequestBody PaymentCreateDTO request
    ) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

         // ⚠️ normalement tu dois fetch via repository

        PaymentDTO payment = paymentService.processPayment(
                purchase,
                request.getAmount(),
                request.getMethod(),
                request.getTransactionId(),
                request.isSuccess()
        );
        return ResponseEntity.ok(ResponseUtil.success("Payment success", payment, null) );
    }

    // Voir les achats d’un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PurchaseDTO>>> getUserPurchases(@PathVariable Long userId) {
       List<PurchaseDTO> purchaseDTOS =  purchaseService.getUserPurchases(userId);
        return ResponseEntity.ok(ResponseUtil.success("get list Purchase success", purchaseDTOS, null));
    }
}

