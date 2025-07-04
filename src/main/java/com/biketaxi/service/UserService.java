package com.biketaxi.service;

import com.biketaxi.entity.User;
import com.biketaxi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getProfile(User user) {
        return user;
    }

    @Transactional
    public User updateProfile(User user, User update) {
        // Update basic information
        if (update.getFirstName() != null) {
            user.setFirstName(update.getFirstName());
        }
        if (update.getLastName() != null) {
            user.setLastName(update.getLastName());
        }
        if (update.getEmail() != null) {
            user.setEmail(update.getEmail());
        }
        if (update.getPhoneNumber() != null) {
            user.setPhoneNumber(update.getPhoneNumber());
        }
        if (update.getDateOfBirth() != null) {
            user.setDateOfBirth(update.getDateOfBirth());
        }
        if (update.getGender() != null) {
            user.setGender(update.getGender());
        }
        if (update.getProfilePhoto() != null) {
            user.setProfilePhoto(update.getProfilePhoto());
        }
        if (update.getEmergencyContactName() != null) {
            user.setEmergencyContactName(update.getEmergencyContactName());
        }
        if (update.getEmergencyContactPhone() != null) {
            user.setEmergencyContactPhone(update.getEmergencyContactPhone());
        }
        if (update.getPreferredLanguage() != null) {
            user.setPreferredLanguage(update.getPreferredLanguage());
        }
        if (update.getReferralCode() != null) {
            user.setReferralCode(update.getReferralCode());
        }
        if (update.getReferredBy() != null) {
            user.setReferredBy(update.getReferredBy());
        }
        
        return userRepository.save(user);
    }
} 