package com.labplatform.lab_platform_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalyticsDashboardDTO {

    // Equipment
    private long totalEquipment;
    private long availableEquipment;
    private long bookedEquipment;
    private long maintenanceEquipment;
    private long highDemandEquipmentCount;
    private long idleEquipmentCount;

    // Booking
    private long totalBookings;
    private long approvedBookings;
    private long pendingBookings;
    private long rejectedBookings;

    // Sharing
    private long totalSharingRequests;
    private long approvedSharingRequests;
    private long pendingSharingRequests;
    private long rejectedSharingRequests;

    // Maintenance
    private long totalMaintenanceRecords;
    private long completedMaintenance;
    private long inProgressMaintenance;

    // Notifications & Billing
    private long unreadNotifications;
    private double totalUtilizationHours;
    private double totalBilledAmount;
    private long pendingUserRequests;

    // Recommendations Engine
    private List<String> recommendations;
}