package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.RegisterRequest;
import com.labplatform.lab_platform_backend.entity.Institution;
import com.labplatform.lab_platform_backend.entity.Laboratory;
import com.labplatform.lab_platform_backend.entity.Role;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.InstitutionRepository;
import com.labplatform.lab_platform_backend.repository.LaboratoryRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       InstitutionRepository institutionRepository,
                       LaboratoryRepository laboratoryRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Assign Institution only if required
        if (request.getRole() != Role.SYSTEM_ADMIN) {

            Institution institution = institutionRepository.findById(request.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found"));

            user.setInstitution(institution);
        }

        // Assign Laboratory only for lab-level roles
        if (request.getLaboratoryId() != null &&
                (request.getRole() == Role.RESEARCHER
                        || request.getRole() == Role.LAB_TECHNICIAN
                        || request.getRole() == Role.LAB_MANAGER
                        || request.getRole() == Role.DEPARTMENT_HEAD)) {

            Laboratory laboratory = laboratoryRepository.findById(request.getLaboratoryId())
                    .orElseThrow(() -> new RuntimeException("Laboratory not found"));

            user.setLaboratory(laboratory);
        }

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByInstitution(Long institutionId) {
        return userRepository.findByInstitutionId(institutionId);
    }

    public List<User> getUsersByLaboratory(Long laboratoryId) {
        return userRepository.findByLaboratoryId(laboratoryId);
    }
}