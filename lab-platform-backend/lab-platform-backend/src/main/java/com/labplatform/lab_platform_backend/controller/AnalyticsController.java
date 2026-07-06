package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;
import com.labplatform.lab_platform_backend.service.AnalyticsService;
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

    @GetMapping("/dashboard")
    public AnalyticsDashboardDTO getDashboardAnalytics() {

        return analyticsService.getDashboardAnalytics();

    }
}