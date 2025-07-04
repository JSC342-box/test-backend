package com.biketaxi.controller;

import com.biketaxi.entity.Rating;
import com.biketaxi.entity.User;
import com.biketaxi.entity.Ride;
import com.biketaxi.repository.RatingRepository;
import com.biketaxi.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class RatingController {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    // Constructor injection
    public RatingController(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/ratings")
    public Rating submitRating(@AuthenticationPrincipal User byUser, @RequestBody RatingRequest req) {
        Rating rating = new Rating();
        rating.setRide(req.getRide());
        rating.setByUser(byUser);
        rating.setToUser(userRepository.findById(req.getToUserId()).orElseThrow());
        rating.setScore(req.getScore());
        rating.setComment(req.getComment());
        rating.setCreatedAt(Instant.now());
        return ratingRepository.save(rating);
    }

    @GetMapping("/api/ratings/{rideId}")
    public List<Rating> getRatingsForRide(@PathVariable UUID rideId) {
        return ratingRepository.findByRideId(rideId);
    }

    @GetMapping("/api/drivers/me/rating")
    public double getDriverAverageRating(@AuthenticationPrincipal User user) {
        List<Rating> ratings = ratingRepository.findByToUserId(user.getId());
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }

    public static class RatingRequest {
        private Ride ride;
        private UUID toUserId;
        private int score;
        private String comment;

        // Getters and Setters
        public Ride getRide() {
            return ride;
        }

        public void setRide(Ride ride) {
            this.ride = ride;
        }

        public UUID getToUserId() {
            return toUserId;
        }

        public void setToUserId(UUID toUserId) {
            this.toUserId = toUserId;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
