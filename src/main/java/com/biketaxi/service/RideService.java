package com.biketaxi.service;

import com.biketaxi.dto.RideRequestDto;
import com.biketaxi.entity.*;
import com.biketaxi.entity.enums.RideStatus;
import com.biketaxi.repository.DriverRepository;
import com.biketaxi.repository.RideRepository;
import com.biketaxi.repository.UserRepository;
import com.biketaxi.util.FareCalculator;
import com.biketaxi.websocket.RideNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    
    @Autowired(required = false)
    private RideNotificationService notificationService;

    public RideService(RideRepository rideRepository, UserRepository userRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public Ride requestRide(User rider, RideRequestDto request) {
        Ride ride = new Ride();
        ride.setRider(rider);
        ride.setPickupLat(request.getPickupLat());
        ride.setPickupLng(request.getPickupLng());
        ride.setDropLat(request.getDropLat());
        ride.setDropLng(request.getDropLng());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(Instant.now());
        // Estimate fare
        var fare = FareCalculator.estimateFare(
                Math.sqrt(Math.pow(request.getPickupLat() - request.getDropLat(), 2) + Math.pow(request.getPickupLng() - request.getDropLng(), 2)) * 111,
                request.getVehicleType(),
                1.0,
                15
        );
        ride.setEstimatedFare(fare.getTotalFare());
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(rider.getId().toString(), saved);
        }
        return saved;
    }

    @Transactional
    public Ride acceptRide(String rideId, Driver driver) {
        Ride ride = getRideOrThrow(rideId);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(Instant.now());
        // Generate OTP
        String otp = String.valueOf(1000 + new java.util.Random().nextInt(9000));
        ride.setOtp(otp);
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(ride.getRider().getId().toString(), saved);
            notificationService.notifyDriver(driver.getId().toString(), saved);
        }
        return saved;
    }

    @Transactional
    public Ride verifyOtpAndStartRide(String rideId, Driver driver, String otp) {
        Ride ride = getRideOrThrow(rideId);
        if (!StringUtils.hasText(otp) || !otp.equals(ride.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }
        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(Instant.now());
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(ride.getRider().getId().toString(), saved);
            notificationService.notifyDriver(driver.getId().toString(), saved);
        }
        return saved;
    }

    @Transactional
    public Ride startRide(String rideId, Driver driver) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(Instant.now());
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(ride.getRider().getId().toString(), saved);
            notificationService.notifyDriver(driver.getId().toString(), saved);
        }
        return saved;
    }

    @Transactional
    public Ride completeRide(String rideId, Driver driver) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(Instant.now());
        
        // Update driver statistics
        if (ride.getDriver() != null) {
            Driver rideDriver = ride.getDriver();
            rideDriver.setTotalRides(rideDriver.getTotalRides() + 1);
            driverRepository.save(rideDriver);
        }
        
        // Update rider statistics
        if (ride.getRider() != null) {
            User rider = ride.getRider();
            rider.setTotalRides(rider.getTotalRides() + 1);
            userRepository.save(rider);
        }
        
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(ride.getRider().getId().toString(), saved);
            notificationService.notifyDriver(driver.getId().toString(), saved);
        }
        return saved;
    }

    @Transactional
    public Ride cancelRide(String rideId, User user) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(RideStatus.CANCELLED);
        Ride saved = rideRepository.save(ride);
        if (notificationService != null) {
            notificationService.notifyRider(ride.getRider().getId().toString(), saved);
            if (ride.getDriver() != null) {
                notificationService.notifyDriver(ride.getDriver().getId().toString(), saved);
            }
        }
        return saved;
    }

    private Ride getRideOrThrow(String rideId) {
        return rideRepository.findById(UUID.fromString(rideId)).orElseThrow(() -> new RuntimeException("Ride not found"));
    }
} 