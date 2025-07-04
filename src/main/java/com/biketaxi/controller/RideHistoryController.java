package com.biketaxi.controller;

import com.biketaxi.entity.Ride;
import com.biketaxi.entity.User;
import com.biketaxi.entity.Driver;
import com.biketaxi.repository.RideRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class RideHistoryController {
    private final RideRepository rideRepository;

    public RideHistoryController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @GetMapping("/api/users/me/rides")
    public List<Ride> getRiderHistory(@AuthenticationPrincipal User user) {
        return rideRepository.findAll().stream().filter(r -> r.getRider().getId().equals(user.getId())).toList();
    }

    @GetMapping("/api/drivers/me/rides")
    public List<Ride> getDriverHistory(@AuthenticationPrincipal Driver driver) {
        return rideRepository.findAll().stream().filter(r -> r.getDriver() != null && r.getDriver().getId().equals(driver.getId())).toList();
    }

    @GetMapping("/api/rides/{rideId}")
    public Ride getRideDetail(@PathVariable UUID rideId) {
        return rideRepository.findById(rideId).orElseThrow();
    }
} 