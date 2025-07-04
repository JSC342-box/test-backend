package com.biketaxi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String clerkUserId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column
    private String gender;

    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private Instant registrationDate;

    @Column
    private Double rating = 0.0;

    @Column(name = "total_rides")
    private Integer totalRides = 0;

    @Column(name = "wallet_balance")
    private Double walletBalance = 0.0;

    @Column(name = "referral_code", unique = true)
    private String referralCode;

    @Column(name = "referred_by")
    private UUID referredBy;

    @Column(name = "preferred_language")
    private String preferredLanguage = "en";

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public User() {}

    public UUID getId() { 
        return id; 
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }

    public String getClerkUserId() { 
        return clerkUserId; 
    }
    
    public void setClerkUserId(String clerkUserId) { 
        this.clerkUserId = clerkUserId; 
    }

    public String getFirstName() { 
        return firstName; 
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }
    
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPhoneNumber() { 
        return phoneNumber; 
    }
    
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber; 
    }

    public LocalDate getDateOfBirth() { 
        return dateOfBirth; 
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) { 
        this.dateOfBirth = dateOfBirth; 
    }

    public String getGender() { 
        return gender; 
    }
    
    public void setGender(String gender) { 
        this.gender = gender; 
    }

    public byte[] getProfilePhoto() { 
        return profilePhoto; 
    }
    
    public void setProfilePhoto(byte[] profilePhoto) { 
        this.profilePhoto = profilePhoto; 
    }

    public Boolean getIsVerified() { 
        return isVerified; 
    }
    
    public void setIsVerified(Boolean isVerified) { 
        this.isVerified = isVerified; 
    }

    public Boolean getIsActive() { 
        return isActive; 
    }
    
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive; 
    }

    public Instant getRegistrationDate() { 
        return registrationDate; 
    }
    
    public void setRegistrationDate(Instant registrationDate) { 
        this.registrationDate = registrationDate; 
    }

    public Double getRating() { 
        return rating; 
    }
    
    public void setRating(Double rating) { 
        this.rating = rating; 
    }

    public Integer getTotalRides() { 
        return totalRides; 
    }
    
    public void setTotalRides(Integer totalRides) { 
        this.totalRides = totalRides; 
    }

    public Double getWalletBalance() { 
        return walletBalance; 
    }
    
    public void setWalletBalance(Double walletBalance) { 
        this.walletBalance = walletBalance; 
    }

    public String getReferralCode() { 
        return referralCode; 
    }
    
    public void setReferralCode(String referralCode) { 
        this.referralCode = referralCode; 
    }

    public UUID getReferredBy() { 
        return referredBy; 
    }
    
    public void setReferredBy(UUID referredBy) { 
        this.referredBy = referredBy; 
    }

    public String getPreferredLanguage() { 
        return preferredLanguage; 
    }
    
    public void setPreferredLanguage(String preferredLanguage) { 
        this.preferredLanguage = preferredLanguage; 
    }

    public String getEmergencyContactName() { 
        return emergencyContactName; 
    }
    
    public void setEmergencyContactName(String emergencyContactName) { 
        this.emergencyContactName = emergencyContactName; 
    }

    public String getEmergencyContactPhone() { 
        return emergencyContactPhone; 
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) { 
        this.emergencyContactPhone = emergencyContactPhone; 
    }

    public Role getRole() { 
        return role; 
    }
    
    public void setRole(Role role) { 
        this.role = role; 
    }

    public Instant getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Instant createdAt) { 
        this.createdAt = createdAt; 
    }

    public Instant getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(Instant updatedAt) { 
        this.updatedAt = updatedAt; 
    }

    public enum Role {
        RIDER, DRIVER, ADMIN
    }
} 