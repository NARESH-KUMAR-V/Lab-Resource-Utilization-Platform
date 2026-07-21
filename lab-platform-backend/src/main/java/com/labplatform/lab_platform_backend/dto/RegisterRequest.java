package com.labplatform.lab_platform_backend.dto;

import com.labplatform.lab_platform_backend.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;

    private String email;

    private String password;

    private Role role;

    private Long institutionId;

    private Long laboratoryId;
}