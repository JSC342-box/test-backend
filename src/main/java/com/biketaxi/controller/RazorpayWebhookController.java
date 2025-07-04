package com.biketaxi.controller;

import com.biketaxi.entity.Payment;
import com.biketaxi.entity.Ride;
import com.biketaxi.repository.PaymentRepository;
import com.biketaxi.repository.RideRepository;
import com.biketaxi.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhooks/razorpay")
@CrossOrigin(origins = "*")
public class RazorpayWebhookController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    public RazorpayWebhookController(PaymentService paymentService,
                                   PaymentRepository paymentRepository,
                                   RideRepository rideRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
    }

    /**
     * Handle Razorpay webhook events
     */
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                               @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            // Verify webhook signature (when webhook secret is configured)
            if (webhookSecret != null && !webhookSecret.isEmpty()) {
                boolean isValidSignature = com.razorpay.Utils.verifyWebhookSignature(
                        payload, signature, webhookSecret);
                
                if (!isValidSignature) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
                }
            }

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            switch (eventType) {
                case "payment.captured":
                    handlePaymentCaptured(event);
                    break;
                case "payment.failed":
                    handlePaymentFailed(event);
                    break;
                case "refund.processed":
                    handleRefundProcessed(event);
                    break;
                default:
                    // Log unhandled event types
                    System.out.println("Unhandled webhook event: " + eventType);
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            System.err.println("Webhook processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook processing failed");
        }
    }

    private void handlePaymentCaptured(JSONObject event) {
        try {
            JSONObject payment = event.getJSONObject("payload").getJSONObject("payment");
            String paymentId = payment.getString("id");
            String orderId = payment.getString("order_id");

            // Find and update payment record
            Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
            if (paymentOpt.isPresent()) {
                Payment paymentRecord = paymentOpt.get();
                paymentRecord.setRazorpayPaymentId(paymentId);
                paymentRecord.setStatus("CAPTURED");
                paymentRecord.setUpdatedAt(Instant.now());
                paymentRepository.save(paymentRecord);

                // Update ride status
                Ride ride = paymentRecord.getRide();
                ride.setStatus(com.biketaxi.entity.enums.RideStatus.COMPLETED);
                rideRepository.save(ride);

                System.out.println("Payment captured for ride: " + ride.getId());
            }
        } catch (Exception e) {
            System.err.println("Error handling payment captured: " + e.getMessage());
        }
    }

    private void handlePaymentFailed(JSONObject event) {
        try {
            JSONObject payment = event.getJSONObject("payload").getJSONObject("payment");
            String orderId = payment.getString("order_id");

            // Find and update payment record
            Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(orderId);
            if (paymentOpt.isPresent()) {
                Payment paymentRecord = paymentOpt.get();
                paymentRecord.setStatus("FAILED");
                paymentRecord.setUpdatedAt(Instant.now());
                paymentRepository.save(paymentRecord);

                System.out.println("Payment failed for ride: " + paymentRecord.getRide().getId());
            }
        } catch (Exception e) {
            System.err.println("Error handling payment failed: " + e.getMessage());
        }
    }

    private void handleRefundProcessed(JSONObject event) {
        try {
            JSONObject refund = event.getJSONObject("payload").getJSONObject("refund");
            String paymentId = refund.getString("payment_id");

            // Find payment by Razorpay payment ID and update status
            // Note: You might need to add a method to find by razorpayPaymentId
            // For now, this is a placeholder
            System.out.println("Refund processed for payment: " + paymentId);
        } catch (Exception e) {
            System.err.println("Error handling refund processed: " + e.getMessage());
        }
    }
} 