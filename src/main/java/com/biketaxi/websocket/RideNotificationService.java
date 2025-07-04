package com.biketaxi.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Service;

@Service
public class RideNotificationService {
    private final SocketIOServer socketIOServer;

    public RideNotificationService(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public void notifyDriver(String driverId, Object payload) {
        socketIOServer.getRoomOperations("driver_" + driverId).sendEvent("notification", payload);
    }

    public void notifyRider(String riderId, Object payload) {
        socketIOServer.getRoomOperations("rider_" + riderId).sendEvent("notification", payload);
    }

    public void notifyRide(String rideId, Object payload) {
        socketIOServer.getRoomOperations("ride_" + rideId).sendEvent("notification", payload);
    }
} 