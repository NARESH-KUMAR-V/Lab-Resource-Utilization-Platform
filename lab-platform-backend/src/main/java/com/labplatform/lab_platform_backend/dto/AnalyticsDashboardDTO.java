package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

@Data
public class AnalyticsDashboardDTO {

    // Equipment
    private long totalEquipment;
    private long availableEquipment;
    private long bookedEquipment;
    private long maintenanceEquipment;

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

    // Notifications
    private long unreadNotifications;

    // Utilization
    private double totalUtilizationHours;
}