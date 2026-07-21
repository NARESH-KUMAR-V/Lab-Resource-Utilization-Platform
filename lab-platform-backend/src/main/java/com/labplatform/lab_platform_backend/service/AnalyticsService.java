package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;

public interface AnalyticsService {

    // Overall analytics (Admin / Manager)
    AnalyticsDashboardDTO getDashboardAnalytics(String userEmail);

    // Researcher's personal analytics
    AnalyticsDashboardDTO getMyDashboardAnalytics(String userEmail);

}