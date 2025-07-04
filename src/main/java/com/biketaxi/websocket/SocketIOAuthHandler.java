package com.biketaxi.websocket;

import com.biketaxi.entity.User;
import com.biketaxi.service.ClerkService;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SocketIOAuthHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOAuthHandler.class);
    
    private final ClerkService clerkService;
    
    @Value("${socketio.auth.required:false}")
    private boolean authRequired;
    
    @Value("${socketio.auth.bypass:true}")
    private boolean authBypass;

    @Autowired
    public SocketIOAuthHandler(ClerkService clerkService) {
        this.clerkService = clerkService;
    }

    public AuthorizationListener createAuthListener() {
        return new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                try {
                    logger.info("Socket.IO authentication attempt from: {}", data.getAddress());

                    // If authentication is bypassed, allow all connections
                    if (authBypass) {
                        logger.info("Authentication bypassed - allowing connection for testing");
                        
                        // Set default test user information
                        data.getHttpHeaders().add("user-id", "test-user-id");
                        data.getHttpHeaders().add("user-role", "TEST");
                        data.getHttpHeaders().add("user-email", "test@example.com");
                        
                        return true;
                    }

                    // Get token from URL param or Authorization header
                    String token = data.getSingleUrlParam("token");
                    if (token == null || token.isEmpty()) {
                        token = data.getHttpHeaders().get("Authorization");
                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7);
                            logger.debug("Token found in Authorization header");
                        }
                    }

                    // If no token provided and auth is not required, allow connection
                    if ((token == null || token.isEmpty()) && !authRequired) {
                        logger.info("No token provided but authentication not required - allowing connection");
                        
                        // Set default test user information
                        data.getHttpHeaders().add("user-id", "anonymous-user-id");
                        data.getHttpHeaders().add("user-role", "ANONYMOUS");
                        data.getHttpHeaders().add("user-email", "anonymous@example.com");
                        
                        return true;
                    }

                    if (token == null || token.isEmpty()) {
                        logger.warn("No token provided in Socket.IO connection");
                        return false;
                    }

                    // Verify the token and get user using ClerkService
                    Optional<User> userOpt = clerkService.verifyTokenAndGetUser(token);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        logger.info("Socket.IO authentication successful for user: {}", user.getEmail());

                        // Store user information in handshake data for later use
                        data.getHttpHeaders().add("user-id", user.getId().toString());
                        data.getHttpHeaders().add("user-role", user.getRole().name());
                        data.getHttpHeaders().add("user-email", user.getEmail());
                        data.getHttpHeaders().add("clerk-user-id", user.getClerkUserId());

                        return true;
                    } else {
                        logger.warn("Invalid token provided for Socket.IO connection");
                        return false;
                    }

                } catch (Exception e) {
                    logger.error("Error during Socket.IO authentication: {}", e.getMessage(), e);
                    return false;
                }
            }
        };
    }

    public User getUserFromClient(SocketIOClient client) {
        try {
            String userId = client.getHandshakeData().getHttpHeaders().get("user-id");
            if (userId != null && !"test-user-id".equals(userId) && !"anonymous-user-id".equals(userId)) {
                // You might want to fetch the user from database again
                // or store it in a session map for better performance
                logger.debug("Getting user from client with ID: {}", userId);
                return null; // For now, return null - implement as needed
            }
        } catch (Exception e) {
            logger.error("Error getting user from client: {}", e.getMessage(), e);
        }
        return null;
    }

    public String getUserRoleFromClient(SocketIOClient client) {
        try {
            String role = client.getHandshakeData().getHttpHeaders().get("user-role");
            logger.debug("Getting user role from client: {}", role);
            return role != null ? role : "TEST";
        } catch (Exception e) {
            logger.error("Error getting user role from client: {}", e.getMessage(), e);
            return "TEST";
        }
    }
    
    public String getUserEmailFromClient(SocketIOClient client) {
        try {
            String email = client.getHandshakeData().getHttpHeaders().get("user-email");
            logger.debug("Getting user email from client: {}", email);
            return email != null ? email : "test@example.com";
        } catch (Exception e) {
            logger.error("Error getting user email from client: {}", e.getMessage(), e);
            return "test@example.com";
        }
    }
    
    public String getClerkUserIdFromClient(SocketIOClient client) {
        try {
            String clerkUserId = client.getHandshakeData().getHttpHeaders().get("clerk-user-id");
            logger.debug("Getting Clerk user ID from client: {}", clerkUserId);
            return clerkUserId;
        } catch (Exception e) {
            logger.error("Error getting Clerk user ID from client: {}", e.getMessage(), e);
            return null;
        }
    }
} 