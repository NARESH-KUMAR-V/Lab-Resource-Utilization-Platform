package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.DashboardStatsResponse;
import com.labplatform.lab_platform_backend.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getDashboardStats(Authentication authentication) {

        return dashboardService.getDashboardStats(authentication.getName());

    }
}