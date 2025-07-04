package com.biketaxi.controller;

import com.biketaxi.dto.FareEstimateDto;
import com.biketaxi.dto.RideRequestDto;
import com.biketaxi.service.FareService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class FareController {

    private final FareService fareService;

    // Manual constructor injection
    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    public FareEstimateDto estimateFare(@RequestBody RideRequestDto request) {
        // For demo, use a fixed surge multiplier and estimated time
        double surgeMultiplier = 1.0; // TODO: fetch from surge service
        int estimatedTime = 15;       // TODO: calculate based on distance

        double distanceKm = Math.sqrt(
                Math.pow(request.getPickupLat() - request.getDropLat(), 2) +
                Math.pow(request.getPickupLng() - request.getDropLng(), 2)
        ) * 111; // rough estimate

        return fareService.estimateFare(
                distanceKm,
                request.getVehicleType(),
                surgeMultiplier,
                estimatedTime
        );
    }
}
