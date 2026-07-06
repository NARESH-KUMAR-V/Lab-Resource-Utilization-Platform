package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}