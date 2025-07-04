package com.biketaxi.controller;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.User;
import com.biketaxi.entity.Vehicle;
import com.biketaxi.entity.enums.DriverStatus;
import com.biketaxi.entity.enums.VehicleType;
import com.biketaxi.repository.DriverRepository;
import com.biketaxi.repository.UserRepository;
import com.biketaxi.repository.VehicleRepository;
import com.biketaxi.service.UserService;
import com.biketaxi.util.ImageUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    public RegistrationController(UserRepository userRepository,
                                DriverRepository driverRepository,
                                VehicleRepository vehicleRepository,
                                UserService userService) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.userService = userService;
    }

    /**
     * Register a new user (rider) with profile photo
     */
    @PostMapping("/user")
    public ResponseEntity<?> registerUser(
            @RequestParam("clerkUserId") String clerkUserId,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "emergencyContactName", required = false) String emergencyContactName,
            @RequestParam(value = "emergencyContactPhone", required = false) String emergencyContactPhone,
            @RequestParam(value = "preferredLanguage", defaultValue = "en") String preferredLanguage,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto) {
        
        try {
            // Validate required Clerk ID
            if (clerkUserId == null || clerkUserId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Clerk User ID is required"));
            }

            // Check if user already exists
            if (userRepository.findByClerkUserId(clerkUserId).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User with this Clerk ID already exists"));
            }

            // Check email uniqueness only if provided
            if (email != null && !email.trim().isEmpty()) {
                if (userRepository.findByEmail(email).isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "User with this email already exists"));
                }
            }

            // Check phone number uniqueness only if provided
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "User with this phone number already exists"));
                }
            }

            // Validate profile photo if provided
            byte[] profilePhotoData = null;
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                if (!ImageUtil.isValidImage(profilePhoto)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid profile photo format"));
                }
                profilePhotoData = ImageUtil.convertToByteArray(profilePhoto);
            }

            // Create new user
            User user = new User();
            user.setClerkUserId(clerkUserId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            
            // Parse date of birth only if provided
            if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
                try {
                    user.setDateOfBirth(LocalDate.parse(dateOfBirth));
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid date format. Use YYYY-MM-DD"));
                }
            }
            
            user.setGender(gender);
            user.setEmergencyContactName(emergencyContactName);
            user.setEmergencyContactPhone(emergencyContactPhone);
            user.setPreferredLanguage(preferredLanguage);
            user.setProfilePhoto(profilePhotoData);
            user.setRole(User.Role.RIDER);
            user.setIsVerified(false);
            user.setIsActive(true);

            User savedUser = userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("clerkUserId", savedUser.getClerkUserId());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            response.put("hasProfilePhoto", profilePhotoData != null);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to register user: " + e.getMessage()));
        }
    }

    /**
     * Register a new driver with profile photo and license photo
     */
    @PostMapping("/driver")
    public ResponseEntity<?> registerDriver(
            @RequestParam("clerkUserId") String clerkUserId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("gender") String gender,
            @RequestParam("address") String address,
            @RequestParam("licenseNumber") String licenseNumber,
            @RequestParam("licenseExpiryDate") String licenseExpiryDate,
            @RequestParam("aadharNumber") String aadharNumber,
            @RequestParam("panNumber") String panNumber,
            @RequestParam("bankAccountNumber") String bankAccountNumber,
            @RequestParam("ifscCode") String ifscCode,
            @RequestParam("accountHolderName") String accountHolderName,
            @RequestParam("emergencyContactName") String emergencyContactName,
            @RequestParam("emergencyContactPhone") String emergencyContactPhone,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestParam(value = "licensePhoto", required = false) MultipartFile licensePhoto) {
        
        try {
            // Validate required Clerk ID
            if (clerkUserId == null || clerkUserId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Clerk User ID is required"));
            }

            // Check if driver already exists
            if (driverRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Driver with this email already exists"));
            }

            if (driverRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Driver with this phone number already exists"));
            }

            if (driverRepository.findByLicenseNumber(licenseNumber).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Driver with this license number already exists"));
            }

            // Validate images if provided
            byte[] profilePicData = null;
            byte[] licensePhotoData = null;

            if (profilePic != null && !profilePic.isEmpty()) {
                if (!ImageUtil.isValidImage(profilePic)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid profile photo format"));
                }
                profilePicData = ImageUtil.convertToByteArray(profilePic);
            }

            if (licensePhoto != null && !licensePhoto.isEmpty()) {
                if (!ImageUtil.isValidImage(licensePhoto)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid license photo format"));
                }
                licensePhotoData = ImageUtil.convertToByteArray(licensePhoto);
            }

            // Create new driver
            Driver driver = new Driver();
            driver.setFirstName(firstName);
            driver.setLastName(lastName);
            driver.setEmail(email);
            driver.setPhoneNumber(phoneNumber);
            driver.setDateOfBirth(LocalDate.parse(dateOfBirth));
            driver.setGender(gender);
            driver.setAddress(address);
            driver.setLicenseNumber(licenseNumber);
            driver.setLicenseExpiryDate(LocalDate.parse(licenseExpiryDate));
            driver.setAadharNumber(aadharNumber);
            driver.setPanNumber(panNumber);
            driver.setBankAccountNumber(bankAccountNumber);
            driver.setIfscCode(ifscCode);
            driver.setAccountHolderName(accountHolderName);
            driver.setEmergencyContactName(emergencyContactName);
            driver.setEmergencyContactPhone(emergencyContactPhone);
            driver.setProfilePic(profilePicData);
            driver.setLicensePhoto(licensePhotoData);
            driver.setIsVerified(false);
            driver.setStatus(DriverStatus.OFFLINE);
            driver.setIsOnline(false);

            Driver savedDriver = driverRepository.save(driver);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Driver registered successfully");
            response.put("driverId", savedDriver.getId());
            response.put("clerkUserId", clerkUserId);
            response.put("email", savedDriver.getEmail());
            response.put("status", "PENDING_VERIFICATION");
            response.put("hasProfilePic", profilePicData != null);
            response.put("hasLicensePhoto", licensePhotoData != null);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to register driver: " + e.getMessage()));
        }
    }

    /**
     * Register a new vehicle for a driver with vehicle photos and documents
     */
    @PostMapping("/vehicle")
    public ResponseEntity<?> registerVehicle(
            @RequestParam("driverId") String driverId,
            @RequestParam("vehicleType") String vehicleType,
            @RequestParam("model") String model,
            @RequestParam("year") String year,
            @RequestParam("color") String color,
            @RequestParam("licensePlate") String licensePlate,
            @RequestParam("registrationNumber") String registrationNumber,
            @RequestParam("insurancePolicyNumber") String insurancePolicyNumber,
            @RequestParam("insuranceExpiryDate") String insuranceExpiryDate,
            @RequestParam(value = "pollutionExpiryDate", required = false) String pollutionExpiryDate,
            @RequestParam(value = "vehiclePhoto", required = false) MultipartFile vehiclePhoto,
            @RequestParam(value = "registrationCertificate", required = false) MultipartFile registrationCertificate,
            @RequestParam(value = "insuranceDocument", required = false) MultipartFile insuranceDocument,
            @RequestParam(value = "pollutionCertificate", required = false) MultipartFile pollutionCertificate) {
        
        try {
            // Check if driver exists
            Driver driver = driverRepository.findById(UUID.fromString(driverId))
                    .orElse(null);
            if (driver == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Driver not found"));
            }

            // Check if vehicle already exists
            if (vehicleRepository.findByLicensePlate(licensePlate).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Vehicle with this license plate already exists"));
            }

            if (vehicleRepository.findByRegistrationNumber(registrationNumber).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Vehicle with this registration number already exists"));
            }

            // Validate images if provided
            byte[] vehiclePhotoData = null;
            byte[] registrationCertificateData = null;
            byte[] insuranceDocumentData = null;
            byte[] pollutionCertificateData = null;

            if (vehiclePhoto != null && !vehiclePhoto.isEmpty()) {
                if (!ImageUtil.isValidImage(vehiclePhoto)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid vehicle photo format"));
                }
                vehiclePhotoData = ImageUtil.convertToByteArray(vehiclePhoto);
            }

            if (registrationCertificate != null && !registrationCertificate.isEmpty()) {
                if (!ImageUtil.isValidImage(registrationCertificate)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid registration certificate format"));
                }
                registrationCertificateData = ImageUtil.convertToByteArray(registrationCertificate);
            }

            if (insuranceDocument != null && !insuranceDocument.isEmpty()) {
                if (!ImageUtil.isValidImage(insuranceDocument)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid insurance document format"));
                }
                insuranceDocumentData = ImageUtil.convertToByteArray(insuranceDocument);
            }

            if (pollutionCertificate != null && !pollutionCertificate.isEmpty()) {
                if (!ImageUtil.isValidImage(pollutionCertificate)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid pollution certificate format"));
                }
                pollutionCertificateData = ImageUtil.convertToByteArray(pollutionCertificate);
            }

            // Create new vehicle
            Vehicle vehicle = new Vehicle();
            vehicle.setDriver(driver);
            vehicle.setVehicleType(VehicleType.valueOf(vehicleType.toUpperCase()));
            vehicle.setModel(model);
            vehicle.setYear(Integer.parseInt(year));
            vehicle.setColor(color);
            vehicle.setLicensePlate(licensePlate);
            vehicle.setRegistrationNumber(registrationNumber);
            vehicle.setInsurancePolicyNumber(insurancePolicyNumber);
            vehicle.setInsuranceExpiryDate(LocalDate.parse(insuranceExpiryDate));
            vehicle.setPollutionExpiryDate(pollutionExpiryDate != null ? LocalDate.parse(pollutionExpiryDate) : null);
            vehicle.setVehiclePhoto(vehiclePhotoData);
            vehicle.setRegistrationCertificate(registrationCertificateData);
            vehicle.setInsuranceDocument(insuranceDocumentData);
            vehicle.setPollutionCertificate(pollutionCertificateData);
            vehicle.setIsVerified(false);

            Vehicle savedVehicle = vehicleRepository.save(vehicle);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vehicle registered successfully");
            response.put("vehicleId", savedVehicle.getId());
            response.put("licensePlate", savedVehicle.getLicensePlate());
            response.put("driverId", savedVehicle.getDriver().getId());
            response.put("status", "PENDING_VERIFICATION");
            response.put("hasVehiclePhoto", vehiclePhotoData != null);
            response.put("hasRegistrationCertificate", registrationCertificateData != null);
            response.put("hasInsuranceDocument", insuranceDocumentData != null);
            response.put("hasPollutionCertificate", pollutionCertificateData != null);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to register vehicle: " + e.getMessage()));
        }
    }

    /**
     * Verify driver registration (admin only)
     */
    @PutMapping("/driver/{driverId}/verify")
    public ResponseEntity<?> verifyDriver(@PathVariable UUID driverId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElse(null);
            if (driver == null) {
                return ResponseEntity.notFound().build();
            }

            driver.setIsVerified(true);
            driverRepository.save(driver);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Driver verified successfully");
            response.put("driverId", driver.getId());
            response.put("status", "VERIFIED");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to verify driver: " + e.getMessage()));
        }
    }

    /**
     * Verify vehicle registration (admin only)
     */
    @PutMapping("/vehicle/{vehicleId}/verify")
    public ResponseEntity<?> verifyVehicle(@PathVariable UUID vehicleId) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }

            vehicle.setIsVerified(true);
            vehicleRepository.save(vehicle);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vehicle verified successfully");
            response.put("vehicleId", vehicle.getId());
            response.put("status", "VERIFIED");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to verify vehicle: " + e.getMessage()));
        }
    }
} 