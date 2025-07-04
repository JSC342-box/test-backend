package com.biketaxi.controller;

import com.biketaxi.dto.RideRequestDto;
import com.biketaxi.entity.enums.VehicleType;
import com.biketaxi.service.DriverMatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideMatchingController {

    private final DriverMatchingService driverMatchingService;

    public RideMatchingController(DriverMatchingService driverMatchingService) {
        this.driverMatchingService = driverMatchingService;
    }

    @PostMapping("/nearby")
    public NearbyDriversResponse findNearbyDrivers(@RequestBody RideRequestDto request) {
        int[] searchRadii = {3, 4, 6};
        for (int radius : searchRadii) {
            List<DriverMatchingService.NearbyDriver> drivers = driverMatchingService.findNearbyDrivers(
                    request.getPickupLat(),
                    request.getPickupLng(),
                    request.getVehicleType(),
                    request.getPreferredRating(),
                    radius,
                    5
            );
            if (!drivers.isEmpty()) {
                return new NearbyDriversResponse(drivers, radius, drivers.size());
            }
        }
        return new NearbyDriversResponse(List.of(), 6, 0);
    }

    public static class NearbyDriversResponse {
        private List<DriverMatchingService.NearbyDriver> drivers;
        private int searchRadiusUsed;
        private int totalDriversFound;

        public NearbyDriversResponse(List<DriverMatchingService.NearbyDriver> drivers, int searchRadiusUsed, int totalDriversFound) {
            this.drivers = drivers;
            this.searchRadiusUsed = searchRadiusUsed;
            this.totalDriversFound = totalDriversFound;
        }

        // Getters
        public List<DriverMatchingService.NearbyDriver> getDrivers() {
            return drivers;
        }

        public int getSearchRadiusUsed() {
            return searchRadiusUsed;
        }

        public int getTotalDriversFound() {
            return totalDriversFound;
        }

        
        public void setDrivers(List<DriverMatchingService.NearbyDriver> drivers) {
            this.drivers = drivers;
        }

        public void setSearchRadiusUsed(int searchRadiusUsed) {
            this.searchRadiusUsed = searchRadiusUsed;
        }

        public void setTotalDriversFound(int totalDriversFound) {
            this.totalDriversFound = totalDriversFound;
        }
    }
}