package com.biketaxi.controller;

import com.biketaxi.entity.RideLocation;
import com.biketaxi.repository.RideLocationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides/{rideId}/locations")
public class RideLocationController {
    private final RideLocationRepository rideLocationRepository;

    public RideLocationController(RideLocationRepository rideLocationRepository) {
        this.rideLocationRepository = rideLocationRepository;
    }

    @GetMapping
    public List<RideLocation> getRideLocations(@PathVariable UUID rideId) {
        return rideLocationRepository.findByRideIdOrderByTimestampAsc(rideId);
    }
} 