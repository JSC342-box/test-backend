package com.biketaxi.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ride_locations")
public class RideLocation {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private double lat;
    private double lng;
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    public enum LocationType {
        PICKUP, DROPOFF, WAYPOINT
    }

    // Default constructor
    public RideLocation() {}

    // Constructor with required fields
    public RideLocation(Ride ride, double lat, double lng, LocationType locationType) {
        this.ride = ride;
        this.lat = lat;
        this.lng = lng;
        this.locationType = locationType;
        this.timestamp = Instant.now();
    }

    // Constructor with all fields
    public RideLocation(Ride ride, double lat, double lng, Instant timestamp, LocationType locationType) {
        this.ride = ride;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
        this.locationType = locationType;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
} 