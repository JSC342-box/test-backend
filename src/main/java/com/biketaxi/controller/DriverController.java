package com.biketaxi.controller;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.Vehicle;
import com.biketaxi.entity.enums.DriverStatus;
import com.biketaxi.service.DriverService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers/me")
public class DriverController {

    private final DriverService driverService;

    // Manual constructor
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    @PreAuthorize("hasRole('DRIVER')")
    public Driver getDriver(@AuthenticationPrincipal Driver driver) {
        return driverService.getDriver(driver);
    }

    @PostMapping("/vehicle")
    @PreAuthorize("hasRole('DRIVER')")
    public Vehicle addOrUpdateVehicle(@AuthenticationPrincipal Driver driver, @RequestBody Vehicle vehicle) {
        return driverService.addOrUpdateVehicle(driver, vehicle);
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DRIVER')")
    public Driver updateStatus(@AuthenticationPrincipal Driver driver, @RequestBody StatusRequest statusRequest) {
        return driverService.updateStatus(driver, statusRequest.getStatus());
    }

    @PostMapping("/location")
    @PreAuthorize("hasRole('DRIVER')")
    public void updateLocation(@AuthenticationPrincipal Driver driver, @RequestBody LocationRequest locationRequest) {
        driverService.updateLocation(driver, locationRequest.getLat(), locationRequest.getLng());
    }

    public static class StatusRequest {
        private DriverStatus status;

        public DriverStatus getStatus() {
            return status;
        }

        public void setStatus(DriverStatus status) {
            this.status = status;
        }
    }

    public static class LocationRequest {
        private double lat;
        private double lng;

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
    }
}
