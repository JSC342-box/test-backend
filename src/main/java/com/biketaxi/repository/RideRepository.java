package com.biketaxi.repository;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.Ride;
import com.biketaxi.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> findByRider(User rider);
    List<Ride> findByDriver(Driver driver);
    List<Ride> findByStatus(com.biketaxi.entity.enums.RideStatus status);
} 