package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.DashboardStatsResponse;

public interface DashboardService {

    DashboardStatsResponse getDashboardStats(String userEmail);

}