package com.biketaxi.controller;

import com.biketaxi.dto.RideRequestDto;
import com.biketaxi.entity.Ride;
import com.biketaxi.entity.User;
import com.biketaxi.entity.Driver;
import com.biketaxi.service.RideService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    // Manual constructor
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/request")
    @PreAuthorize("hasRole('RIDER')")
    public Ride requestRide(@AuthenticationPrincipal User rider, @RequestBody RideRequestDto request) {
        return rideService.requestRide(rider, request);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public Ride acceptRide(@PathVariable("id") String rideId, @AuthenticationPrincipal Driver driver) {
        return rideService.acceptRide(rideId, driver);
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public Ride startRide(@PathVariable("id") String rideId, @AuthenticationPrincipal Driver driver) {
        return rideService.startRide(rideId, driver);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public Ride completeRide(@PathVariable("id") String rideId, @AuthenticationPrincipal Driver driver) {
        return rideService.completeRide(rideId, driver);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    public Ride cancelRide(@PathVariable("id") String rideId, @AuthenticationPrincipal User user) {
        return rideService.cancelRide(rideId, user);
    }

    @PostMapping("/{id}/verify-otp")
    @PreAuthorize("hasRole('DRIVER')")
    public Ride verifyOtpAndStartRide(@PathVariable("id") String rideId,
                                      @AuthenticationPrincipal Driver driver,
                                      @RequestBody OtpRequest otpRequest) {
        return rideService.verifyOtpAndStartRide(rideId, driver, otpRequest.getOtp());
    }

    public static class OtpRequest {
        private String otp;

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
}
