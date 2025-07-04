package com.biketaxi.repository;

import com.biketaxi.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByClerkUserId(String clerkUserId);
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPhoneNumber(String phoneNumber);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    List<Driver> findByStatus(com.biketaxi.entity.enums.DriverStatus status);
} 