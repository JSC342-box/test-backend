package com.biketaxi.controller;

import com.biketaxi.config.ClerkConfig;
import com.biketaxi.entity.User;
import com.biketaxi.service.ClerkService;
import com.biketaxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ClerkAuthController {

    private final ClerkConfig clerkConfig;
    private final ClerkService clerkService;
    private final UserService userService;

    @Autowired
    public ClerkAuthController(ClerkConfig clerkConfig, ClerkService clerkService, UserService userService) {
        this.clerkConfig = clerkConfig;
        this.clerkService = clerkService;
        this.userService = userService;
    }

    /**
     * Get Clerk configuration for frontend
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getClerkConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publishableKey", clerkConfig.getPublishableKey());
        config.put("jwtIssuer", clerkConfig.getJwtIssuer());
        return ResponseEntity.ok(config);
    }

    /**
     * Get current authenticated user's profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid authentication"));
    }

    /**
     * Test endpoint that uses the authenticated user's token
     */
    @PostMapping("/test-endpoint")
    public ResponseEntity<?> testAuthenticatedEndpoint(@RequestBody Map<String, Object> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint accessed successfully with authenticated token");
        response.put("user", user);
        response.put("request", request);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test ride request with authenticated user
     */
    @PostMapping("/test-ride-request")
    public ResponseEntity<?> testRideRequest(@RequestBody Map<String, Object> rideRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();
        
        // Simulate ride request processing
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ride request processed successfully");
        response.put("userId", user.getId());
        response.put("userEmail", user.getEmail());
        response.put("rideRequest", rideRequest);
        response.put("status", "PENDING");
        response.put("estimatedFare", 25.50);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test driver profile access
     */
    @GetMapping("/test-driver-profile")
    public ResponseEntity<?> testDriverProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();
        
        // Check if user has DRIVER role
        if (user.getRole() != User.Role.DRIVER) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Driver role required."));
        }

        Map<String, Object> driverProfile = new HashMap<>();
        driverProfile.put("driverId", user.getId());
        driverProfile.put("firstName", user.getFirstName());
        driverProfile.put("lastName", user.getLastName());
        driverProfile.put("email", user.getEmail());
        driverProfile.put("phone", user.getPhoneNumber());
        driverProfile.put("status", "ONLINE");
        driverProfile.put("rating", user.getRating());
        driverProfile.put("totalRides", user.getTotalRides());
        driverProfile.put("isVerified", user.getIsVerified());
        driverProfile.put("isActive", user.getIsActive());
        driverProfile.put("registrationDate", user.getRegistrationDate());
        driverProfile.put("walletBalance", user.getWalletBalance());
        
        return ResponseEntity.ok(driverProfile);
    }

    /**
     * Test admin access
     */
    @GetMapping("/test-admin-access")
    public ResponseEntity<?> testAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User user = (User) authentication.getPrincipal();
        
        // Check if user has ADMIN role
        if (user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Admin role required."));
        }

        Map<String, Object> adminData = new HashMap<>();
        adminData.put("adminId", user.getId());
        adminData.put("name", user.getFirstName());
        adminData.put("email", user.getEmail());
        adminData.put("totalUsers", 1250);
        adminData.put("totalDrivers", 89);
        adminData.put("totalRides", 5670);
        adminData.put("totalRevenue", 125000.00);
        
        return ResponseEntity.ok(adminData);
    }

    /**
     * Update user role (for testing purposes)
     */
    @PostMapping("/update-role")
    public ResponseEntity<?> updateUserRole(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        User currentUser = (User) authentication.getPrincipal();
        String newRole = request.get("role");
        
        try {
            User.Role role = User.Role.valueOf(newRole.toUpperCase());
            User updatedUser = clerkService.updateUserRole(currentUser.getClerkUserId(), role);
            
            return ResponseEntity.ok(Map.of(
                "message", "Role updated successfully",
                "user", updatedUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + newRole));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update role: " + e.getMessage()));
        }
    }

    /**
     * Get authentication status and user info
     */
    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> status = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated()) {
            status.put("authenticated", true);
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                status.put("user", user);
                status.put("role", user.getRole());
                status.put("permissions", getPermissionsForRole(user.getRole()));
            }
        } else {
            status.put("authenticated", false);
        }
        
        return ResponseEntity.ok(status);
    }

    private Map<String, Boolean> getPermissionsForRole(User.Role role) {
        Map<String, Boolean> permissions = new HashMap<>();
        
        switch (role) {
            case RIDER:
                permissions.put("requestRide", true);
                permissions.put("viewProfile", true);
                permissions.put("viewRideHistory", true);
                permissions.put("rateDriver", true);
                permissions.put("accessDriverProfile", false);
                permissions.put("accessAdminPanel", false);
                break;
            case DRIVER:
                permissions.put("requestRide", false);
                permissions.put("viewProfile", true);
                permissions.put("viewRideHistory", true);
                permissions.put("rateDriver", false);
                permissions.put("accessDriverProfile", true);
                permissions.put("accessAdminPanel", false);
                break;
            case ADMIN:
                permissions.put("requestRide", false);
                permissions.put("viewProfile", true);
                permissions.put("viewRideHistory", true);
                permissions.put("rateDriver", false);
                permissions.put("accessDriverProfile", true);
                permissions.put("accessAdminPanel", true);
                break;
        }
        
        return permissions;
    }
} 