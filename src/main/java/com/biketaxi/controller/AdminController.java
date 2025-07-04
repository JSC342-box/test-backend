package com.biketaxi.controller;

import com.biketaxi.entity.User;
import com.biketaxi.entity.Driver;
import com.biketaxi.entity.Ride;
import com.biketaxi.entity.Vehicle;
import com.biketaxi.repository.UserRepository;
import com.biketaxi.repository.DriverRepository;
import com.biketaxi.repository.RideRepository;
import com.biketaxi.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final VehicleRepository vehicleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Constructor injection
    public AdminController(UserRepository userRepository,
                           DriverRepository driverRepository,
                           RideRepository rideRepository,
                           VehicleRepository vehicleRepository,
                           RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.vehicleRepository = vehicleRepository;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> listUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findAll();
        return new PageImpl<>(users, pageable, users.size());
    }

    @GetMapping("/rides")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Ride> listRides(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID driverId,
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<Ride> rides = rideRepository.findAll().stream()
                .filter(r -> status == null || r.getStatus().name().equalsIgnoreCase(status))
                .filter(r -> driverId == null || (r.getDriver() != null && r.getDriver().getId().equals(driverId)))
                .filter(r -> from == null || (r.getRequestedAt() != null && r.getRequestedAt().toEpochMilli() >= from))
                .filter(r -> to == null || (r.getRequestedAt() != null && r.getRequestedAt().toEpochMilli() <= to))
                .collect(Collectors.toList());
        return new PageImpl<>(rides, pageable, rides.size());
    }

    @GetMapping("/vehicles")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Vehicle> listVehicles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return new PageImpl<>(vehicles, pageable, vehicles.size());
    }

    @GetMapping("/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public EarningsReport getEarnings() {
        double total = rideRepository.findAll().stream()
                .mapToDouble(r -> r.getFinalFare() != null ? r.getFinalFare() : 0.0)
                .sum();
        long completed = rideRepository.findAll().stream()
                .filter(r -> r.getFinalFare() != null)
                .count();
        return new EarningsReport(total, completed);
    }

    @PutMapping("/surge/update")
    @PreAuthorize("hasRole('ADMIN')")
    public SurgeUpdateResponse updateSurge(@RequestBody SurgeUpdateRequest req) {
        String key = "surge:area:" + req.getAreaName();
        redisTemplate.opsForValue().set(key, req.getMultiplier());
        return new SurgeUpdateResponse(req.getAreaName(), req.getMultiplier());
    }

    @GetMapping("/drivers/online")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OnlineDriverDTO> getOnlineDrivers() {
        Set<String> keys = redisTemplate.keys("driver:location:*");
        if (keys == null) return List.of();
        List<OnlineDriverDTO> online = new ArrayList<>();
        for (String key : keys) {
            Object lat = redisTemplate.opsForHash().get(key, "lat");
            Object lng = redisTemplate.opsForHash().get(key, "lng");
            Object timestamp = redisTemplate.opsForHash().get(key, "timestamp");
            String driverId = key.replace("driver:location:", "");
            
            // Get additional driver information from database
            try {
                UUID driverUUID = UUID.fromString(driverId);
                var driverOpt = driverRepository.findById(driverUUID);
                if (driverOpt.isPresent()) {
                    Driver driver = driverOpt.get();
                    online.add(new OnlineDriverDTO(
                        driverId, 
                        lat, 
                        lng, 
                        timestamp,
                        driver.getFirstName() + " " + driver.getLastName(),
                        driver.getPhoneNumber(),
                        driver.getRating(),
                        driver.getTotalRides(),
                        driver.getStatus().toString()
                    ));
                } else {
                    online.add(new OnlineDriverDTO(driverId, lat, lng, timestamp, null, null, null, null, null));
                }
            } catch (IllegalArgumentException e) {
                online.add(new OnlineDriverDTO(driverId, lat, lng, timestamp, null, null, null, null, null));
            }
        }
        return online;
    }

    // ======= DTOs below =======

    public static class EarningsReport {
        private final double totalRevenue;
        private final long completedRides;

        public EarningsReport(double totalRevenue, long completedRides) {
            this.totalRevenue = totalRevenue;
            this.completedRides = completedRides;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public long getCompletedRides() {
            return completedRides;
        }
    }

    public static class SurgeUpdateRequest {
        private String areaName;
        private double multiplier;

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }
    }

    public static class SurgeUpdateResponse {
        private final String areaName;
        private final double multiplier;

        public SurgeUpdateResponse(String areaName, double multiplier) {
            this.areaName = areaName;
            this.multiplier = multiplier;
        }

        public String getAreaName() {
            return areaName;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    public static class OnlineDriverDTO {
        private final String driverId;
        private final Object lat;
        private final Object lng;
        private final Object timestamp;
        private final String driverName;
        private final String phoneNumber;
        private final Double rating;
        private final Integer totalRides;
        private final String status;

        public OnlineDriverDTO(String driverId, Object lat, Object lng, Object timestamp, String driverName, String phoneNumber, Double rating, Integer totalRides, String status) {
            this.driverId = driverId;
            this.lat = lat;
            this.lng = lng;
            this.timestamp = timestamp;
            this.driverName = driverName;
            this.phoneNumber = phoneNumber;
            this.rating = rating;
            this.totalRides = totalRides;
            this.status = status;
        }

        public String getDriverId() {
            return driverId;
        }

        public Object getLat() {
            return lat;
        }

        public Object getLng() {
            return lng;
        }

        public Object getTimestamp() {
            return timestamp;
        }

        public String getDriverName() {
            return driverName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Double getRating() {
            return rating;
        }

        public Integer getTotalRides() {
            return totalRides;
        }

        public String getStatus() {
            return status;
        }
    }
}
