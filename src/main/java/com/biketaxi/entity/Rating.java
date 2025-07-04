package com.biketaxi.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "by_user")
    private User byUser;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private User toUser;

    private int score;
    private String comment;
    private Instant createdAt;

    // Default constructor
    public Rating() {}

    // Constructor with required fields
    public Rating(Ride ride, User byUser, User toUser, int score) {
        this.ride = ride;
        this.byUser = byUser;
        this.toUser = toUser;
        this.score = score;
        this.createdAt = Instant.now();
    }

    // Constructor with all fields
    public Rating(Ride ride, User byUser, User toUser, int score, String comment) {
        this.ride = ride;
        this.byUser = byUser;
        this.toUser = toUser;
        this.score = score;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getByUser() {
        return byUser;
    }

    public void setByUser(User byUser) {
        this.byUser = byUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
} 