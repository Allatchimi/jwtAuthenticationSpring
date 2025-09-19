package com.kidami.security.services;

import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.enums.PaymentMethod;
import com.kidami.security.enums.PaymentStatus;
import com.kidami.security.models.Payment;
import com.kidami.security.models.Purchase;

import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    @Transactional
    PaymentDTO processPayment(Purchase purchase, BigDecimal amount, PaymentMethod method, String transactionId, boolean success);

    @Transactional
    PaymentDTO createPayment(Long purchaseId, BigDecimal amount, PaymentMethod method, String transactionId);

    @Transactional
    PaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus status);

    List<PaymentDTO> getPaymentsByPurchase(Long purchaseId);

    @Transactional
    PaymentDTO validatePayment(Long paymentId);

    String createCheckoutSession(Long purchaseId, String successUrl, String cancelUrl) throws Exception;

    @Transactional
    Payment registerStripePayment(Session session, Long purchaseId);

}
