package com.kidami.security.controllers;

import com.kidami.security.models.Payment;
import com.kidami.security.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // Vérifier la signature Stripe
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("❌ Invalid signature");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (session != null) {
                    try {
                        Long purchaseId = Long.valueOf(session.getMetadata().get("purchaseId"));

                        // Enregistrer le paiement en base
                        Payment payment = paymentService.registerStripePayment(session, purchaseId);

                        // Valider automatiquement
                        paymentService.validatePayment(payment.getId());
                    } catch (Exception ex) {
                        return ResponseEntity.status(500).body("❌ Error processing payment: " + ex.getMessage());
                    }
                }
                break;

            default:
                System.out.println("⚠️ Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("✅ Event received: " + event.getType());
    }
}
