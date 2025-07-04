package com.biketaxi.dto;

import com.biketaxi.entity.enums.VehicleType;

public class RideRequestDto {
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
    private VehicleType vehicleType;
    private double preferredRating;

    // Default constructor
    public RideRequestDto() {}

    // Constructor with all fields
    public RideRequestDto(double pickupLat, double pickupLng, double dropLat, double dropLng, 
                         VehicleType vehicleType, double preferredRating) {
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.dropLat = dropLat;
        this.dropLng = dropLng;
        this.vehicleType = vehicleType;
        this.preferredRating = preferredRating;
    }

    // Getters and Setters
    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDropLat() {
        return dropLat;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public double getDropLng() {
        return dropLng;
    }

    public void setDropLng(double dropLng) {
        this.dropLng = dropLng;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPreferredRating() {
        return preferredRating;
    }

    public void setPreferredRating(double preferredRating) {
        this.preferredRating = preferredRating;
    }
} 