package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public List<User> getUsersByInstitution(@PathVariable Long institutionId) {
        return userRepository.findByInstitutionId(institutionId);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN') or hasRole('DEPARTMENT_HEAD')")
    public List<User> getUsersByLaboratory(@PathVariable Long laboratoryId) {
        return userRepository.findByLaboratoryId(laboratoryId);
    }
}