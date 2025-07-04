package com.biketaxi.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SocketIOEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOEventHandler.class);
    
    private final SocketIOAuthHandler authHandler;

    @Autowired
    public SocketIOEventHandler(SocketIOAuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        String userRole = authHandler.getUserRoleFromClient(client);
        
        logger.info("Client connected - Session: {}, User: {}, Role: {}", 
                   sessionId, userEmail, userRole);
        
        // Send welcome message
        client.sendEvent("welcome", Map.of(
            "message", "Welcome to Bike Taxi Socket.IO!",
            "sessionId", sessionId,
            "userEmail", userEmail,
            "userRole", userRole
        ));
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        logger.info("Client disconnected - Session: {}, User: {}", sessionId, userEmail);
    }

    @OnEvent("test")
    public void onTest(SocketIOClient client, Map<String, Object> data) {
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        logger.info("Test event received from client - Session: {}, User: {}, Data: {}", 
                   sessionId, userEmail, data);
        
        // Echo back the test message
        client.sendEvent("test", Map.of(
            "message", "Test message received and echoed back!",
            "originalData", data,
            "sessionId", sessionId,
            "userEmail", userEmail,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @OnEvent("join_room")
    public void onJoinRoom(SocketIOClient client, Map<String, String> data) {
        String room = data.get("room");
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        if (room != null) {
            client.joinRoom(room);
            logger.info("Client joined room - Session: {}, User: {}, Room: {}", 
                       sessionId, userEmail, room);
            
            // Notify room members
            client.getNamespace().getRoomOperations(room).sendEvent("user_joined", Map.of(
                "userEmail", userEmail,
                "sessionId", sessionId,
                "room", room
            ));
        }
    }

    @OnEvent("leave_room")
    public void onLeaveRoom(SocketIOClient client, Map<String, String> data) {
        String room = data.get("room");
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        if (room != null) {
            client.leaveRoom(room);
            logger.info("Client left room - Session: {}, User: {}, Room: {}", 
                       sessionId, userEmail, room);
            
            // Notify room members
            client.getNamespace().getRoomOperations(room).sendEvent("user_left", Map.of(
                "userEmail", userEmail,
                "sessionId", sessionId,
                "room", room
            ));
        }
    }

    @OnEvent("updateLocation")
    public void onUpdateLocation(SocketIOClient client, Map<String, Object> data) {
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        logger.info("Location update from client - Session: {}, User: {}, Location: {}", 
                   sessionId, userEmail, data);
        
        // Broadcast location update to relevant clients (e.g., drivers in the area)
        // This is a simplified version - you would implement your own logic here
        client.getNamespace().getBroadcastOperations().sendEvent("locationUpdate", Map.of(
            "userEmail", userEmail,
            "location", data,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @OnEvent("rideNotification")
    public void onRideNotification(SocketIOClient client, Map<String, Object> data) {
        String sessionId = client.getSessionId().toString();
        String userEmail = authHandler.getUserEmailFromClient(client);
        
        logger.info("Ride notification from client - Session: {}, User: {}, Notification: {}", 
                   sessionId, userEmail, data);
        
        // Process ride notification and broadcast to relevant clients
        // This is a simplified version - you would implement your own logic here
        client.getNamespace().getBroadcastOperations().sendEvent("rideNotification", Map.of(
            "userEmail", userEmail,
            "notification", data,
            "timestamp", System.currentTimeMillis()
        ));
    }
} 