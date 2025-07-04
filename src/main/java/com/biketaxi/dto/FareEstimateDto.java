package com.biketaxi.dto;

public class FareEstimateDto {
    private double baseFare;
    private double distanceCost;
    private double surgeCost;
    private double timeCost;
    private double totalFare;
    private double distanceKm;
    private double surgeMultiplier;
    private String currency;

    // Default constructor
    public FareEstimateDto() {}

    // Constructor with all fields
    public FareEstimateDto(double baseFare, double distanceCost, double surgeCost, double timeCost, 
                          double totalFare, double distanceKm, double surgeMultiplier, String currency) {
        this.baseFare = baseFare;
        this.distanceCost = distanceCost;
        this.surgeCost = surgeCost;
        this.timeCost = timeCost;
        this.totalFare = totalFare;
        this.distanceKm = distanceKm;
        this.surgeMultiplier = surgeMultiplier;
        this.currency = currency;
    }

    // Getters and Setters
    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(double distanceCost) {
        this.distanceCost = distanceCost;
    }

    public double getSurgeCost() {
        return surgeCost;
    }

    public void setSurgeCost(double surgeCost) {
        this.surgeCost = surgeCost;
    }

    public double getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(double timeCost) {
        this.timeCost = timeCost;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public void setSurgeMultiplier(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 