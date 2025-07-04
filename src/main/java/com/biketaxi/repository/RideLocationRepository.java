package com.biketaxi.repository;

import com.biketaxi.entity.RideLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideLocationRepository extends JpaRepository<RideLocation, UUID> {
    List<RideLocation> findByRideId(UUID rideId);
    List<RideLocation> findByRideIdOrderByTimestampAsc(UUID rideId);
} 