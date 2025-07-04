package com.biketaxi.controller;

import com.biketaxi.entity.User;
import com.biketaxi.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER', 'ADMIN')")
    public User getProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER', 'ADMIN')")
    public User updateProfile(@AuthenticationPrincipal User user, @RequestBody User update) {
        return userService.updateProfile(user, update);
    }
} 