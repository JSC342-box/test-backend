package com.biketaxi.controller;

import com.biketaxi.entity.Driver;
import com.biketaxi.entity.User;
import com.biketaxi.entity.Vehicle;
import com.biketaxi.repository.DriverRepository;
import com.biketaxi.repository.UserRepository;
import com.biketaxi.repository.VehicleRepository;
import com.biketaxi.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    // User Profile Photo
    @PostMapping("/user/{userId}/profile")
    public ResponseEntity<String> uploadUserProfilePhoto(
            @PathVariable UUID userId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            user.setProfilePhoto(imageData);
            userRepository.save(user);
            
            return ResponseEntity.ok("Profile photo uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<byte[]> getUserProfilePhoto(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        
        if (user == null || user.getProfilePhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(user.getProfilePhoto());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(user.getProfilePhoto().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(user.getProfilePhoto());
    }
    
    // Driver Profile Photo
    @PostMapping("/driver/{driverId}/profile")
    public ResponseEntity<String> uploadDriverProfilePhoto(
            @PathVariable UUID driverId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Driver driver = driverRepository.findById(driverId)
                    .orElse(null);
            
            if (driver == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            driver.setProfilePic(imageData);
            driverRepository.save(driver);
            
            return ResponseEntity.ok("Driver profile photo uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/driver/{driverId}/profile")
    public ResponseEntity<byte[]> getDriverProfilePhoto(@PathVariable UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElse(null);
        
        if (driver == null || driver.getProfilePic() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(driver.getProfilePic());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(driver.getProfilePic().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(driver.getProfilePic());
    }
    
    // Driver License Photo
    @PostMapping("/driver/{driverId}/license")
    public ResponseEntity<String> uploadDriverLicensePhoto(
            @PathVariable UUID driverId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Driver driver = driverRepository.findById(driverId)
                    .orElse(null);
            
            if (driver == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            driver.setLicensePhoto(imageData);
            driverRepository.save(driver);
            
            return ResponseEntity.ok("Driver license photo uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/driver/{driverId}/license")
    public ResponseEntity<byte[]> getDriverLicensePhoto(@PathVariable UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElse(null);
        
        if (driver == null || driver.getLicensePhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(driver.getLicensePhoto());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(driver.getLicensePhoto().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(driver.getLicensePhoto());
    }
    
    // Vehicle Photos
    @PostMapping("/vehicle/{vehicleId}/photo")
    public ResponseEntity<String> uploadVehiclePhoto(
            @PathVariable UUID vehicleId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            vehicle.setVehiclePhoto(imageData);
            vehicleRepository.save(vehicle);
            
            return ResponseEntity.ok("Vehicle photo uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/photo")
    public ResponseEntity<byte[]> getVehiclePhoto(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(null);
        
        if (vehicle == null || vehicle.getVehiclePhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(vehicle.getVehiclePhoto());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(vehicle.getVehiclePhoto().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(vehicle.getVehiclePhoto());
    }
    
    // Vehicle Registration Certificate
    @PostMapping("/vehicle/{vehicleId}/registration")
    public ResponseEntity<String> uploadVehicleRegistration(
            @PathVariable UUID vehicleId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            vehicle.setRegistrationCertificate(imageData);
            vehicleRepository.save(vehicle);
            
            return ResponseEntity.ok("Vehicle registration certificate uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/registration")
    public ResponseEntity<byte[]> getVehicleRegistration(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(null);
        
        if (vehicle == null || vehicle.getRegistrationCertificate() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(vehicle.getRegistrationCertificate());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(vehicle.getRegistrationCertificate().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(vehicle.getRegistrationCertificate());
    }
    
    // Vehicle Insurance Document
    @PostMapping("/vehicle/{vehicleId}/insurance")
    public ResponseEntity<String> uploadVehicleInsurance(
            @PathVariable UUID vehicleId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            vehicle.setInsuranceDocument(imageData);
            vehicleRepository.save(vehicle);
            
            return ResponseEntity.ok("Vehicle insurance document uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/insurance")
    public ResponseEntity<byte[]> getVehicleInsurance(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(null);
        
        if (vehicle == null || vehicle.getInsuranceDocument() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(vehicle.getInsuranceDocument());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(vehicle.getInsuranceDocument().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(vehicle.getInsuranceDocument());
    }
    
    // Vehicle Pollution Certificate
    @PostMapping("/vehicle/{vehicleId}/pollution")
    public ResponseEntity<String> uploadVehiclePollution(
            @PathVariable UUID vehicleId,
            @RequestParam("image") MultipartFile image) {
        
        try {
            if (!ImageUtil.isValidImage(image)) {
                return ResponseEntity.badRequest().body("Invalid image file");
            }
            
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElse(null);
            
            if (vehicle == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageData = ImageUtil.convertToByteArray(image);
            vehicle.setPollutionCertificate(imageData);
            vehicleRepository.save(vehicle);
            
            return ResponseEntity.ok("Vehicle pollution certificate uploaded successfully");
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}/pollution")
    public ResponseEntity<byte[]> getVehiclePollution(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElse(null);
        
        if (vehicle == null || vehicle.getPollutionCertificate() == null) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = ImageUtil.getContentTypeFromBytes(vehicle.getPollutionCertificate());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(vehicle.getPollutionCertificate().length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(vehicle.getPollutionCertificate());
    }
} 