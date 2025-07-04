package com.biketaxi.service;

import com.biketaxi.dto.FareEstimateDto;
import com.biketaxi.entity.enums.VehicleType;
import com.biketaxi.util.FareCalculator;
import org.springframework.stereotype.Service;

@Service
public class FareService {
    public FareEstimateDto estimateFare(double distanceKm, VehicleType type, double surgeMultiplier, int estimatedTimeMinutes) {
        return FareCalculator.estimateFare(distanceKm, type, surgeMultiplier, estimatedTimeMinutes);
    }
} 