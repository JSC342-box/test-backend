package com.biketaxi.entity;

import com.biketaxi.entity.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "Driver is required")
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @NotNull(message = "Vehicle type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2100, message = "Year cannot exceed 2100")
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color cannot exceed 50 characters")
    @Column(nullable = false)
    private String color;

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}$", message = "License plate must be in valid Indian format")
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number cannot exceed 20 characters")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "registration_certificate")
    private byte[] registrationCertificate;

    @NotBlank(message = "Insurance policy number is required")
    @Size(max = 50, message = "Insurance policy number cannot exceed 50 characters")
    @Column(name = "insurance_policy_number", nullable = false)
    private String insurancePolicyNumber;

    @NotNull(message = "Insurance expiry date is required")
    @Column(name = "insurance_expiry_date", nullable = false)
    private LocalDate insuranceExpiryDate;

    @Column(name = "insurance_document")
    private byte[] insuranceDocument;

    @Column(name = "pollution_certificate")
    private byte[] pollutionCertificate;

    @Column(name = "pollution_expiry_date")
    private LocalDate pollutionExpiryDate;

    @Column(name = "vehicle_photo")
    private byte[] vehiclePhoto;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private java.time.Instant updatedAt;

    // Default constructor
    public Vehicle() {}

    // Constructor with required fields
    public Vehicle(Driver driver, VehicleType vehicleType, String model, Integer year, 
                   String color, String licensePlate, String registrationNumber, 
                   String insurancePolicyNumber, LocalDate insuranceExpiryDate) {
        this.driver = driver;
        this.vehicleType = vehicleType;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlate = licensePlate;
        this.registrationNumber = registrationNumber;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public byte[] getRegistrationCertificate() {
        return registrationCertificate;
    }

    public void setRegistrationCertificate(byte[] registrationCertificate) {
        this.registrationCertificate = registrationCertificate;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public byte[] getInsuranceDocument() {
        return insuranceDocument;
    }

    public void setInsuranceDocument(byte[] insuranceDocument) {
        this.insuranceDocument = insuranceDocument;
    }

    public byte[] getPollutionCertificate() {
        return pollutionCertificate;
    }

    public void setPollutionCertificate(byte[] pollutionCertificate) {
        this.pollutionCertificate = pollutionCertificate;
    }

    public LocalDate getPollutionExpiryDate() {
        return pollutionExpiryDate;
    }

    public void setPollutionExpiryDate(LocalDate pollutionExpiryDate) {
        this.pollutionExpiryDate = pollutionExpiryDate;
    }

    public byte[] getVehiclePhoto() {
        return vehiclePhoto;
    }

    public void setVehiclePhoto(byte[] vehiclePhoto) {
        this.vehiclePhoto = vehiclePhoto;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public java.time.Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.Instant createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
} 