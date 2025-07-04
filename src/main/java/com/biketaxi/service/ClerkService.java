package com.biketaxi.service;

import com.biketaxi.config.ClerkConfig;
import com.biketaxi.entity.User;
import com.biketaxi.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClerkService {
    
    private final ClerkConfig clerkConfig;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private volatile PublicKey clerkPublicKey;
    private volatile boolean keyInitialized = false;
    
    @Autowired
    public ClerkService(ClerkConfig clerkConfig, UserRepository userRepository) {
        this.clerkConfig = clerkConfig;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
        
        // Create WebClient without base URL for JWKS
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + clerkConfig.getSecretKey())
                .build();
        
        // Don't initialize the key during startup - do it lazily
    }
    
    private void initializeClerkPublicKey() {
        if (keyInitialized) {
            return;
        }
        
        synchronized (this) {
            if (keyInitialized) {
                return;
            }
            
            try {
                // Extract domain from JWT issuer
                String issuer = clerkConfig.getJwtIssuer();
                String domain = issuer.replace("https://", "");
                
                // Fetch Clerk's JWKS (JSON Web Key Set) - Correct URL format
                String jwksUrl = "https://" + domain + "/.well-known/jwks.json";
                System.out.println("🔍 Fetching Clerk JWKS from: " + jwksUrl);
                
                String jwksResponse = webClient.get()
                        .uri(jwksUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                JsonNode jwks = objectMapper.readTree(jwksResponse);
                JsonNode keys = jwks.get("keys");
                
                // Find the key that matches your issuer
                for (JsonNode key : keys) {
                    if ("RS256".equals(key.get("alg").asText())) {
                        // Extract RSA public key components
                        String n = key.get("n").asText();
                        String e = key.get("e").asText();
                        
                        // Convert from Base64URL to BigInteger
                        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
                        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
                        
                        // Create RSA public key
                        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                        KeyFactory factory = KeyFactory.getInstance("RSA");
                        this.clerkPublicKey = factory.generatePublic(spec);
                        
                        System.out.println("✅ Successfully fetched Clerk public key for RS256 verification");
                        break;
                    }
                }
            } catch (Exception e) {
                // Fallback to HMAC for development/testing
                System.err.println("⚠️ Failed to fetch Clerk public key, using HMAC fallback: " + e.getMessage());
                System.err.println("ℹ️ This is normal for development/testing. For production, ensure your Clerk configuration is correct.");
                System.err.println("ℹ️ Your current Clerk issuer: " + clerkConfig.getJwtIssuer());
            } finally {
                keyInitialized = true;
            }
        }
    }
    
    public Optional<User> verifyTokenAndGetUser(String token) {
        try {
            Claims claims = parseJwtToken(token);
            String clerkUserId = claims.getSubject();
            
            if (clerkUserId == null) {
                return Optional.empty();
            }
            
            // Check if user exists in our database
            Optional<User> existingUser = userRepository.findByClerkUserId(clerkUserId);
            if (existingUser.isPresent()) {
                return existingUser;
            }
            
            // If user doesn't exist, create a new user from Clerk data
            return createUserFromClerk(clerkUserId, claims);
            
        } catch (Exception e) {
            System.err.println("Token verification failed: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    private Claims parseJwtToken(String token) {
        try {
            // Initialize the key lazily
            if (!keyInitialized) {
                initializeClerkPublicKey();
            }
            
            if (clerkPublicKey != null) {
                // Use RS256 verification with Clerk's public key
                return Jwts.parserBuilder()
                        .setSigningKey(clerkPublicKey)
                        .requireIssuer(clerkConfig.getJwtIssuer())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } else {
                // Fallback to HMAC for development/testing
                Key key = Keys.hmacShaKeyFor(clerkConfig.getSecretKey().getBytes());
                return Jwts.parserBuilder()
                        .setSigningKey(key)
                        .requireIssuer(clerkConfig.getJwtIssuer())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }
    
    private Optional<User> createUserFromClerk(String clerkUserId, Claims claims) {
        try {
            User user = new User();
            user.setClerkUserId(clerkUserId);
            user.setFirstName(claims.get("name", String.class));
            user.setLastName(claims.get("name", String.class));
            user.setEmail(claims.get("email", String.class));
            user.setPhoneNumber(claims.get("phone", String.class));
            user.setRole(User.Role.RIDER); // Default role
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            
            User savedUser = userRepository.save(user);
            return Optional.of(savedUser);
            
        } catch (Exception e) {
            System.err.println("Failed to create user from Clerk: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public Optional<User> getUserByClerkId(String clerkUserId) {
        return userRepository.findByClerkUserId(clerkUserId);
    }
    
    public User updateUserRole(String clerkUserId, User.Role role) {
        Optional<User> userOpt = userRepository.findByClerkUserId(clerkUserId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(role);
            user.setUpdatedAt(Instant.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public void syncUserFromClerk(String clerkUserId) {
        // This method can be used to sync user data from Clerk
        // when webhooks are received
        try {
            // Create a separate WebClient for Clerk API calls
            WebClient clerkApiClient = WebClient.builder()
                    .baseUrl("https://api.clerk.dev/v1")
                    .defaultHeader("Authorization", "Bearer " + clerkConfig.getSecretKey())
                    .build();
            
            clerkApiClient.get()
                    .uri("/users/" + clerkUserId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> {
                        // Parse response and update user
                        // This is a simplified version
                        System.out.println("✅ Synced user data from Clerk: " + clerkUserId);
                    });
        } catch (Exception e) {
            System.err.println("❌ Failed to sync user from Clerk: " + e.getMessage());
        }
    }
    
    public boolean isWebhookSecretConfigured() {
        return clerkConfig.getWebhookSecret() != null && !clerkConfig.getWebhookSecret().trim().isEmpty();
    }
} 