package com.biketaxi.repository;

import com.biketaxi.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByDriverId(UUID driverId);
    List<Vehicle> findByVehicleType(com.biketaxi.entity.enums.VehicleType vehicleType);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
} 