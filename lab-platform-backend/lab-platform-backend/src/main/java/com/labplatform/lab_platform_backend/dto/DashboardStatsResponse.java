package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

@Data
public class DashboardStatsResponse {

    private long totalEquipment;

    private long availableEquipment;

    private long bookedEquipment;

    private long maintenanceEquipment;

    private long myBookings;
}