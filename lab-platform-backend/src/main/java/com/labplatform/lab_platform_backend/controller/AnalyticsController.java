package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;
import com.labplatform.lab_platform_backend.service.AnalyticsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Overall analytics (Admin / Manager)
    @GetMapping("/dashboard")
    public AnalyticsDashboardDTO getDashboardAnalytics(
            Authentication authentication) {

        return analyticsService.getDashboardAnalytics(
                authentication.getName());

    }

    // Researcher's personal analytics
    @GetMapping("/my-dashboard")
    public AnalyticsDashboardDTO getMyDashboardAnalytics(
            Authentication authentication) {

        return analyticsService.getMyDashboardAnalytics(
                authentication.getName());

    }

}