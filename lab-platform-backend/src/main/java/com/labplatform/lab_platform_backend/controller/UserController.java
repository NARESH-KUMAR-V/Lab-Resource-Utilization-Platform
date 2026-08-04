package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('LAB_TECHNICIAN') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<User> getPendingUsers() {
        return userService.getPendingUsers();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public User approveUser(@PathVariable Long id) {
        return userService.approveUser(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public User rejectUser(@PathVariable Long id) {
        return userService.rejectUser(id);
    }

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public List<User> getUsersByInstitution(@PathVariable Long institutionId) {
        return userService.getUsersByInstitution(institutionId);
    }

    @GetMapping("/laboratory/{laboratoryId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN') or hasRole('DEPARTMENT_HEAD')")
    public List<User> getUsersByLaboratory(@PathVariable Long laboratoryId) {
        return userService.getUsersByLaboratory(laboratoryId);
    }
}