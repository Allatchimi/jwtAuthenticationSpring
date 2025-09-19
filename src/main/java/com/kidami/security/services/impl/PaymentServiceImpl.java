package com.kidami.security.services.impl;

import com.kidami.security.dto.paymentDTO.PaymentDTO;
import com.kidami.security.enums.PaymentMethod;
import com.kidami.security.enums.PaymentStatus;
import com.kidami.security.enums.PurchaseStatus;
import com.kidami.security.mappers.PaymentMapper;
import com.kidami.security.models.Payment;
import com.kidami.security.models.Purchase;
import com.kidami.security.utils.PaymentCompletedEvent;
import com.kidami.security.repository.PaymentRepository;
import com.kidami.security.repository.PurchaseRepository;
import com.kidami.security.services.PaymentService;
import com.kidami.security.services.PurchaseService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PurchaseService purchaseService;
    private final PaymentMapper paymentMapper;
    private final PurchaseRepository purchaseRepository;
    private  ApplicationEventPublisher eventPublisher;


    @Transactional
    @Override
    public PaymentDTO processPayment(Purchase purchase, BigDecimal amount, PaymentMethod method, String transactionId, boolean success) {

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setTransactionId(transactionId);
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        if (success) {
            purchaseService.validatePurchase(purchase);
        }
        PaymentDTO paymentDTO = paymentMapper.toDTO(payment);
        return paymentDTO;
    }

    @Transactional
    @Override
    public PaymentDTO createPayment(Long purchaseId, BigDecimal amount, PaymentMethod method, String transactionId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Achat non trouvé"));

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDateTime.now());

        Payment createdPayment = paymentRepository.save(payment);

        return paymentMapper.toDTO(createdPayment);
    }

    @Transactional
    @Override
    public PaymentDTO updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDTO(updatedPayment);
    }

    @Override
    public List<PaymentDTO> getPaymentsByPurchase(Long purchaseId) {

        List<Payment> payments = paymentRepository.findByPurchaseId(purchaseId);
        return payments.stream()
                .map(payment -> paymentMapper.toDTO(payment))
                .collect(Collectors.toList()) ;
    }
    @Transactional
    @Override
    public PaymentDTO validatePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // ✅ Si paiement validé → marquer l'achat comme "PAID"
        Purchase purchase = payment.getPurchase();
        purchase.setStatus(PurchaseStatus.PAID);
        purchaseRepository.save(purchase);

        // ✅ Appeler l'enrollment automatique
        //enrollmentService.enrollFromPurchase(purchase); // sa creer une boucle
        // ✅ Publier l'événement
        eventPublisher.publishEvent(new PaymentCompletedEvent(purchase));

        return paymentMapper.toDTO(payment);
    }

    @Override
    public String createCheckoutSession(Long purchaseId, String successUrl, String cancelUrl) throws Exception {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Achat non trouvé"));

        List<SessionCreateParams.LineItem> lineItems = purchase.getCourses().stream()
                .map(course -> SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(purchase.getCurrency())
                                .setUnitAmount(course.getPrice().multiply(BigDecimal.valueOf(100)).longValue()) // en cents
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(course.getName())
                                                .build()
                                )
                                .build()
                        )
                        .build())
                .toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .putMetadata("purchaseId", purchaseId.toString())
                .build();

        Session session = Session.create(params);

        return session.getUrl(); // 👉 rediriger frontend vers cette URL
    }

    @Transactional
    @Override
    public Payment registerStripePayment(Session session, Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Achat non trouvé"));

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setAmount(BigDecimal.valueOf(session.getAmountTotal() / 100.0));
        payment.setPaymentMethod(PaymentMethod.STRIPE);
        payment.setTransactionId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
