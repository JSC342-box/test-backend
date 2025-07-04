package com.biketaxi.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    
    private final RazorpayClient razorpayClient;
    private final String keyId;
    private final String keySecret;
    private final String currency;

    public PaymentService(@Value("${razorpay.key.id}") String keyId,
                         @Value("${razorpay.key.secret}") String keySecret,
                         @Value("${razorpay.currency}") String currency) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.currency = currency;
    }

    /**
     * Create a Razorpay order for payment
     */
    public Map<String, Object> createOrder(double amount, String rideId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Convert to paise (smallest currency unit)
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "ride_" + rideId);
        orderRequest.put("notes", Map.of("rideId", rideId));

        Order order = razorpayClient.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("receipt", order.get("receipt"));
        response.put("keyId", keyId);

        return response;
    }

    /**
     * Verify payment signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            return com.razorpay.Utils.verifyWebhookSignature(data, signature, keySecret);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get payment details
     */
    public Map<String, Object> getPaymentDetails(String paymentId) throws RazorpayException {
        com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
        
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("id", payment.get("id"));
        paymentDetails.put("amount", payment.get("amount"));
        paymentDetails.put("currency", payment.get("currency"));
        paymentDetails.put("status", payment.get("status"));
        paymentDetails.put("method", payment.get("method"));
        paymentDetails.put("orderId", payment.get("order_id"));
        paymentDetails.put("createdAt", payment.get("created_at"));

        return paymentDetails;
    }

    /**
     * Process refund
     */
    public Map<String, Object> processRefund(String paymentId, double amount) throws RazorpayException {
        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", (int) (amount * 100)); // Convert to paise

        com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

        Map<String, Object> refundDetails = new HashMap<>();
        refundDetails.put("id", refund.get("id"));
        refundDetails.put("amount", refund.get("amount"));
        refundDetails.put("status", refund.get("status"));
        refundDetails.put("paymentId", refund.get("payment_id"));

        return refundDetails;
    }

    /**
     * Get Razorpay client key ID for frontend
     */
    public String getKeyId() {
        return keyId;
    }
} 