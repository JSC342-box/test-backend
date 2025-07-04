package com.biketaxi.controller;

import com.biketaxi.config.ClerkConfig;
import com.biketaxi.service.ClerkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clerk")
public class ClerkWebhookController {

    private final ClerkService clerkService;
    private final ClerkConfig clerkConfig;

    @Autowired
    public ClerkWebhookController(ClerkService clerkService, ClerkConfig clerkConfig) {
        this.clerkService = clerkService;
        this.clerkConfig = clerkConfig;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleClerkWebhook(
            @RequestHeader(value = "svix-id", required = false) String svixId,
            @RequestHeader(value = "svix-timestamp", required = false) String svixTimestamp,
            @RequestHeader(value = "svix-signature", required = false) String svixSignature,
            @RequestBody String payload
    ) {
        try {
            // Check if webhook secret is configured
            if (!clerkService.isWebhookSecretConfigured()) {
                return ResponseEntity.ok("Webhook secret not configured - skipping signature verification");
            }
            
            // In production, verify the webhook signature using Svix
            // For now, we'll process the webhook without signature verification
            
            // Parse the webhook payload
            // This is a simplified version - you might want to use a proper JSON parser
            if (payload.contains("user.created") || payload.contains("user.updated")) {
                // Extract user ID from payload and sync user data
                // This is a simplified implementation
                String userId = extractUserIdFromPayload(payload);
                if (userId != null) {
                    clerkService.syncUserFromClerk(userId);
                }
            }
            
            return ResponseEntity.ok("Webhook processed successfully");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook processing failed: " + e.getMessage());
        }
    }

    private String extractUserIdFromPayload(String payload) {
        // This is a simplified implementation
        // In production, use a proper JSON parser
        if (payload.contains("\"id\":")) {
            // Extract the user ID from the JSON payload
            // This is a basic string manipulation - use proper JSON parsing in production
            int startIndex = payload.indexOf("\"id\":\"") + 6;
            int endIndex = payload.indexOf("\"", startIndex);
            if (startIndex > 5 && endIndex > startIndex) {
                return payload.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            var userOpt = clerkService.verifyTokenAndGetUser(token);
            
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(userOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }

    @PutMapping("/user/role")
    public ResponseEntity<?> updateUserRole(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        try {
            String token = authHeader.substring(7);
            var userOpt = clerkService.verifyTokenAndGetUser(token);
            
            if (userOpt.isPresent()) {
                String newRole = request.get("role");
                if (newRole != null) {
                    var user = clerkService.updateUserRole(userOpt.get().getClerkUserId(), 
                            com.biketaxi.entity.User.Role.valueOf(newRole.toUpperCase()));
                    return ResponseEntity.ok(user);
                }
            }
            
            return ResponseEntity.badRequest().body("Invalid request");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }
} 