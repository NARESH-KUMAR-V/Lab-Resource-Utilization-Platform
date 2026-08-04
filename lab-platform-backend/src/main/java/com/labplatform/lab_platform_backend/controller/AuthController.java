package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.LoginRequest;
import com.labplatform.lab_platform_backend.dto.LoginResponse;
import com.labplatform.lab_platform_backend.dto.RegisterRequest;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.service.JwtService;
import com.labplatform.lab_platform_backend.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.findByEmail(request.getEmail());

        if (user.getStatus() == UserStatus.PENDING) {
            throw new RuntimeException("Your registration is awaiting System Admin approval.");
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            throw new RuntimeException("Your registration request has been rejected.");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getInstitution() != null ? user.getInstitution().getId() : null,
                user.getLaboratory() != null ? user.getLaboratory().getId() : null
        );
    }
}