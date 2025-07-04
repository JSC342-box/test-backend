package com.biketaxi.controller;

import com.biketaxi.entity.Payment;
import com.biketaxi.entity.Ride;
import com.biketaxi.repository.PaymentRepository;
import com.biketaxi.repository.RideRepository;
import com.biketaxi.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;

    public PaymentController(PaymentService paymentService, 
                           PaymentRepository paymentRepository,
                           RideRepository rideRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
    }

    /**
     * Create a new payment order
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            // Validate ride exists
            Optional<Ride> rideOpt = rideRepository.findById(UUID.fromString(request.getRideId()));
            if (rideOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ride not found"));
            }

            // Create Razorpay order
            Map<String, Object> orderResponse = paymentService.createOrder(request.getAmount(), request.getRideId());

            // Save payment record
            Payment payment = new Payment();
            payment.setRide(rideOpt.get());
            payment.setAmount(request.getAmount());
            payment.setCurrency("INR");
            payment.setStatus("PENDING");
            payment.setRazorpayOrderId((String) orderResponse.get("orderId"));
            payment.setCreatedAt(Instant.now());
            payment.setUpdatedAt(Instant.now());

            paymentRepository.save(payment);

            // Add payment ID to response
            orderResponse.put("paymentId", payment.getId());
            orderResponse.put("message", "Order created successfully");

            return ResponseEntity.ok(orderResponse);

        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create order: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Verify payment and update status
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            // Verify payment signature
            boolean isValid = paymentService.verifyPaymentSignature(
                    request.getOrderId(), 
                    request.getPaymentId(), 
                    request.getSignature()
            );

            if (!isValid) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid payment signature"));
            }

            // Get payment details from Razorpay
            Map<String, Object> paymentDetails = paymentService.getPaymentDetails(request.getPaymentId());

            // Find and update payment record
            Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(request.getOrderId());
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Payment record not found"));
            }

            Payment payment = paymentOpt.get();
            payment.setRazorpayPaymentId(request.getPaymentId());
            payment.setRazorpaySignature(request.getSignature());
            payment.setStatus((String) paymentDetails.get("status"));
            payment.setUpdatedAt(Instant.now());

            paymentRepository.save(payment);

            // Update ride status if payment is successful
            if ("captured".equals(paymentDetails.get("status"))) {
                Ride ride = payment.getRide();
                ride.setStatus(com.biketaxi.entity.enums.RideStatus.COMPLETED);
                rideRepository.save(ride);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment verified successfully");
            response.put("paymentStatus", paymentDetails.get("status"));
            response.put("paymentId", payment.getId());

            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to verify payment: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Get payment details
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(UUID.fromString(paymentId));
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Payment payment = paymentOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", payment.getId());
            response.put("amount", payment.getAmount());
            response.put("currency", payment.getCurrency());
            response.put("status", payment.getStatus());
            response.put("orderId", payment.getRazorpayOrderId());
            response.put("paymentId", payment.getRazorpayPaymentId());
            response.put("rideId", payment.getRide().getId());
            response.put("createdAt", payment.getCreatedAt());
            response.put("updatedAt", payment.getUpdatedAt());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Process refund
     */
    @PostMapping("/refund")
    public ResponseEntity<?> processRefund(@RequestBody RefundRequest request) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(UUID.fromString(request.getPaymentId()));
            if (paymentOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Payment not found"));
            }

            Payment payment = paymentOpt.get();
            if (payment.getRazorpayPaymentId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Payment not completed"));
            }

            Map<String, Object> refundDetails = paymentService.processRefund(
                    payment.getRazorpayPaymentId(), 
                    request.getAmount()
            );

            // Update payment status
            payment.setStatus("REFUNDED");
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund processed successfully");
            response.put("refundDetails", refundDetails);

            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process refund: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Get Razorpay key ID for frontend
     */
    @GetMapping("/key")
    public ResponseEntity<?> getKeyId() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("keyId", paymentService.getKeyId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get key ID: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class CreateOrderRequest {
        private double amount;
        private String rideId;

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getRideId() { return rideId; }
        public void setRideId(String rideId) { this.rideId = rideId; }
    }

    public static class VerifyPaymentRequest {
        private String orderId;
        private String paymentId;
        private String signature;

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }
    }

    public static class RefundRequest {
        private String paymentId;
        private double amount;

        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
}
