package com.biketaxi.service;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.enums.DriverStatus;
import com.biketaxi.entity.enums.VehicleType;
import com.biketaxi.repository.DriverRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverMatchingService {
    private final DriverRepository driverRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public DriverMatchingService(DriverRepository driverRepository, RedisTemplate<String, Object> redisTemplate) {
        this.driverRepository = driverRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<NearbyDriver> findNearbyDrivers(double pickupLat, double pickupLng, VehicleType vehicleType, double preferredRating, int maxRadiusKm, int maxResults) {
        List<Driver> allDrivers = driverRepository.findAll();
        List<NearbyDriver> matches = new ArrayList<>();
        for (Driver driver : allDrivers) {
            if (driver.getStatus() != DriverStatus.AVAILABLE || driver.getRating() == null || driver.getRating() < preferredRating) continue;
            // For demo, assume each driver has one vehicle of the right type
            // In real app, join with vehicle table and check type
            
            // Try to get location from Redis first, then fallback to database
            String key = "driver:location:" + driver.getId();
            Object latObj = redisTemplate.opsForHash().get(key, "lat");
            Object lngObj = redisTemplate.opsForHash().get(key, "lng");
            
            double lat, lng;
            if (latObj != null && lngObj != null) {
                // Use Redis data (more recent)
                lat = Double.parseDouble(latObj.toString());
                lng = Double.parseDouble(lngObj.toString());
            } else if (driver.getCurrentLatitude() != null && driver.getCurrentLongitude() != null) {
                // Fallback to database data
                lat = driver.getCurrentLatitude();
                lng = driver.getCurrentLongitude();
            } else {
                // Skip driver if no location data available
                continue;
            }
            
            double distance = haversine(pickupLat, pickupLng, lat, lng);
            if (distance <= maxRadiusKm) {
                matches.add(new NearbyDriver(driver, distance, estimateEta(distance)));
            }
        }
        return matches.stream()
                .sorted(Comparator.comparingDouble(NearbyDriver::getDistanceKm))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String estimateEta(double distanceKm) {
        int mins = (int) Math.round(distanceKm / 0.5 * 2); // Assume 30km/h avg
        return mins + " mins";
    }

    public static class NearbyDriver {
        private Driver driver;
        private double distanceKm;
        private String estimatedArrival;

        // Default constructor
        public NearbyDriver() {}

        // Constructor with all fields
        public NearbyDriver(Driver driver, double distanceKm, String estimatedArrival) {
            this.driver = driver;
            this.distanceKm = distanceKm;
            this.estimatedArrival = estimatedArrival;
        }

        // Getters and Setters
        public Driver getDriver() {
            return driver;
        }

        public void setDriver(Driver driver) {
            this.driver = driver;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public void setDistanceKm(double distanceKm) {
            this.distanceKm = distanceKm;
        }

        public String getEstimatedArrival() {
            return estimatedArrival;
        }

        public void setEstimatedArrival(String estimatedArrival) {
            this.estimatedArrival = estimatedArrival;
        }
    }
} 