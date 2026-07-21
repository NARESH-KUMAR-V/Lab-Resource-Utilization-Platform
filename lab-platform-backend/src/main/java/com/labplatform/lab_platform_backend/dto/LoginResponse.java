package com.labplatform.lab_platform_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private Long institutionId;
    private Long laboratoryId;

}