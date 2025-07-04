package com.biketaxi.repository;

import com.biketaxi.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByRideId(UUID rideId);
    List<Rating> findByToUserId(UUID toUserId);
    List<Rating> findByByUserId(UUID byUserId);
} 