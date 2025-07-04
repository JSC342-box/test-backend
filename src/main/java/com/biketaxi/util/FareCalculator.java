package com.biketaxi.util;

import com.biketaxi.dto.FareEstimateDto;
import com.biketaxi.entity.enums.VehicleType;

public class FareCalculator {
    // Example config values (should be loaded from config or DB in real app)
    private static final double BASE_FARE_BIKE = 25.0;
    private static final double RATE_PER_KM_BIKE = 8.0;
    private static final double TIME_FACTOR = 1.0; // per minute

    public static FareEstimateDto estimateFare(double distanceKm, VehicleType type, double surgeMultiplier, int estimatedTimeMinutes) {
        double baseFare = BASE_FARE_BIKE;
        double ratePerKm = RATE_PER_KM_BIKE;
        // Add logic for AUTO, CAR if needed
        double distanceCost = ratePerKm * distanceKm;
        double surgeCost = (distanceCost) * (surgeMultiplier - 1);
        double timeCost = TIME_FACTOR * estimatedTimeMinutes;
        double totalFare = baseFare + distanceCost + surgeCost + timeCost;
        FareEstimateDto dto = new FareEstimateDto();
        dto.setBaseFare(baseFare);
        dto.setDistanceCost(distanceCost);
        dto.setSurgeCost(surgeCost);
        dto.setTimeCost(timeCost);
        dto.setTotalFare(totalFare);
        dto.setDistanceKm(distanceKm);
        dto.setSurgeMultiplier(surgeMultiplier);
        dto.setCurrency("INR");
        return dto;
    }
} 