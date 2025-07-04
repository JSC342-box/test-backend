package com.biketaxi.service;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.Vehicle;
import com.biketaxi.entity.enums.DriverStatus;
import com.biketaxi.repository.DriverRepository;
import com.biketaxi.repository.VehicleRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public DriverService(DriverRepository driverRepository, VehicleRepository vehicleRepository,
                        RedisTemplate<String, Object> redisTemplate) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.redisTemplate = redisTemplate;
    }

    public Driver getDriver(Driver driver) {
        return driver;
    }

    @Transactional
    public Vehicle addOrUpdateVehicle(Driver driver, Vehicle vehicle) {
        vehicle.setDriver(driver);
        vehicle.setIsVerified(true);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Driver updateStatus(Driver driver, DriverStatus status) {
        driver.setStatus(status);
        // Update online status based on driver status
        if (status == DriverStatus.ONLINE || status == DriverStatus.AVAILABLE) {
            driver.setIsOnline(true);
        } else {
            driver.setIsOnline(false);
        }
        return driverRepository.save(driver);
    }

    @Transactional
    public void updateLocation(Driver driver, double lat, double lng) {
        // Update location in Redis for real-time access
        String key = "driver:location:" + driver.getId();
        redisTemplate.opsForHash().put(key, "lat", lat);
        redisTemplate.opsForHash().put(key, "lng", lng);
        redisTemplate.opsForHash().put(key, "timestamp", System.currentTimeMillis());
        redisTemplate.expire(key, java.time.Duration.ofSeconds(60));
        
        // Update location in database for persistence
        driver.setCurrentLatitude(lat);
        driver.setCurrentLongitude(lng);
        driverRepository.save(driver);
    }
} 