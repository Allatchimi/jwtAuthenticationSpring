package com.kidami.security.controllers;

import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.enums.PaymentMethod;
import com.kidami.security.enums.PaymentStatus;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.PaymentService;
import com.kidami.security.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(
            @RequestParam Long purchaseId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod method,
            @RequestParam String transactionId) {
        PaymentDTO paymentDTO = paymentService.createPayment(purchaseId, amount, method, transactionId);
        return ResponseEntity.ok(ResponseUtil.success("Paiement créé", paymentDTO, null));
    }

    @PutMapping("/{paymentId}/validate")
    public ResponseEntity<ApiResponse<PaymentDTO>> validatePayment(@PathVariable Long paymentId) {
        PaymentDTO paymentDTO = paymentService.validatePayment(paymentId);
        return ResponseEntity.ok(ResponseUtil.success("Paiement validé et cours inscrits", paymentDTO, null));
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {
        PaymentDTO paymentDTO = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(ResponseUtil.success("Statut mis à jour", paymentDTO, null));
    }

    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByPurchase(@PathVariable Long purchaseId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByPurchase(purchaseId);
        return ResponseEntity.ok(ResponseUtil.success("Liste des paiements récupérée", payments, null));
    }
}
