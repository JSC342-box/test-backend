package com.biketaxi.websocket;

import com.biketaxi.entity.Ride;
import com.biketaxi.entity.RideLocation;
import com.biketaxi.repository.RideRepository;
import com.biketaxi.repository.RideLocationRepository;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class LocationWebSocketController {
    private final SocketIOServer socketIOServer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RideRepository rideRepository;
    private final RideLocationRepository rideLocationRepository;
    
    public LocationWebSocketController(SocketIOServer socketIOServer,
                                     RedisTemplate<String, Object> redisTemplate, 
                                     RideRepository rideRepository,
                                     RideLocationRepository rideLocationRepository) {
        this.socketIOServer = socketIOServer;
        this.redisTemplate = redisTemplate;
        this.rideRepository = rideRepository;
        this.rideLocationRepository = rideLocationRepository;
    }

    @OnEvent("driver_location")
    public void handleLocationUpdate(SocketIOClient client, LocationUpdateMessage message) {
        // Store in Redis
        String redisKey = "ride:location:" + message.getRideId();
        redisTemplate.opsForHash().put(redisKey, "lat", message.getLat());
        redisTemplate.opsForHash().put(redisKey, "lng", message.getLng());
        redisTemplate.opsForHash().put(redisKey, "timestamp", message.getTimestamp());
        redisTemplate.expire(redisKey, java.time.Duration.ofMinutes(10));

        // Broadcast to ride and rider channels
        socketIOServer.getRoomOperations("ride_" + message.getRideId()).sendEvent("location_update", message);
        socketIOServer.getRoomOperations("rider_" + message.getRiderId()).sendEvent("location_update", message);

        // Periodically persist to PostgreSQL (every 5th update)
        if (message.getSequence() % 5 == 0) {
            persistLocation(message);
        }
    }

    private void persistLocation(LocationUpdateMessage message) {
        Ride ride = rideRepository.findById(UUID.fromString(message.getRideId())).orElse(null);
        if (ride != null) {
            RideLocation loc = new RideLocation();
            loc.setRide(ride);
            loc.setLat(message.getLat());
            loc.setLng(message.getLng());
            loc.setTimestamp(java.time.Instant.ofEpochMilli(message.getTimestamp()));
            loc.setLocationType(RideLocation.LocationType.WAYPOINT);
            rideLocationRepository.save(loc);
        }
    }

    public static class LocationUpdateMessage {
        private String rideId;
        private String riderId;
        private String driverId;
        private double lat;
        private double lng;
        private long timestamp;
        private int sequence; // incremented by driver client

        // Default constructor
        public LocationUpdateMessage() {}

        // Constructor with all fields
        public LocationUpdateMessage(String rideId, String riderId, String driverId, 
                                   double lat, double lng, long timestamp, int sequence) {
            this.rideId = rideId;
            this.riderId = riderId;
            this.driverId = driverId;
            this.lat = lat;
            this.lng = lng;
            this.timestamp = timestamp;
            this.sequence = sequence;
        }
        
        // Getters and Setters
        public String getRideId() {
            return rideId;
        }

        public void setRideId(String rideId) {
            this.rideId = rideId;
        }

        public String getRiderId() {
            return riderId;
        }

        public void setRiderId(String riderId) {
            this.riderId = riderId;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }
    }
} 