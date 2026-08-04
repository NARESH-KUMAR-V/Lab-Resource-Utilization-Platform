package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.RegisterRequest;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.InstitutionRepository;
import com.labplatform.lab_platform_backend.repository.LaboratoryRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public UserService(UserRepository userRepository,
                       InstitutionRepository institutionRepository,
                       LaboratoryRepository laboratoryRepository,
                       PasswordEncoder passwordEncoder,
                       NotificationService notificationService,
                       SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
    }

    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setRequestedRole(request.getRole());
        user.setStatus(UserStatus.PENDING);
        user.setDepartment(request.getDepartment());
        user.setCreatedAt(LocalDateTime.now());

        if (request.getInstitutionId() != null) {
            Institution institution = institutionRepository.findById(request.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found"));
            user.setInstitution(institution);
        }

        if (request.getLaboratoryId() != null) {
            Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                    .orElseThrow(() -> new RuntimeException("Laboratory not found"));
            user.setLaboratory(laboratory);
        }

        User savedUser = userRepository.save(user);

        // Notify system admins of new registration request
        notificationService.notifySystemAdmins(
                "New user registration request: " + savedUser.getName() + " (" + savedUser.getEmail() + ") requested role: " + savedUser.getRole() + "."
        );

        return savedUser;
    }

    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        if (user.getRequestedRole() != null) {
            user.setRole(user.getRequestedRole());
        }

        User savedUser = userRepository.save(user);

        notificationService.createNotification(
                savedUser,
                "Your registration request has been approved by System Admin. Your account is now active."
        );

        notificationService.notifySystemAdmins(
                "User account for " + savedUser.getName() + " (" + savedUser.getEmail() + ") has been APPROVED."
        );

        return savedUser;
    }

    public User rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.REJECTED);

        User savedUser = userRepository.save(user);

        notificationService.createNotification(
                savedUser,
                "Your registration request has been rejected by System Admin."
        );

        notificationService.notifySystemAdmins(
                "User account for " + savedUser.getName() + " (" + savedUser.getEmail() + ") has been REJECTED."
        );

        return savedUser;
    }

    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return userRepository.findAll();
        }
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            return userRepository.findByLaboratoryId(user.getLaboratory().getId());
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return userRepository.findAll();
        }
        return userRepository.findByInstitutionId(instId);
    }

    public List<User> getUsersByInstitution(Long institutionId) {
        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.isSystemAdmin(currentUser)) {
            Long userInstId = securityUtil.getUserInstitutionId(currentUser);
            if (userInstId == null || !userInstId.equals(institutionId)) {
                throw new AccessDeniedException("Access denied: You can only view users belonging to your own institution");
            }
        }
        return userRepository.findByInstitutionId(institutionId);
    }

    public List<User> getUsersByLaboratory(Long laboratoryId) {
        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.isSystemAdmin(currentUser)) {
            Laboratory lab = laboratoryRepository.findById(laboratoryId)
                    .orElseThrow(() -> new RuntimeException("Laboratory not found with id: " + laboratoryId));
            Long userInstId = securityUtil.getUserInstitutionId(currentUser);
            Long labInstId = lab.getInstitution() != null ? lab.getInstitution().getId() : null;
            if (userInstId == null || !userInstId.equals(labInstId)) {
                throw new AccessDeniedException("Access denied: You can only view users belonging to laboratories in your institution");
            }
        }
        return userRepository.findByLaboratoryId(laboratoryId);
    }
}