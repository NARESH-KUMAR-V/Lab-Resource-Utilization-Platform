package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

@Data
public class EquipmentUtilizationStatsDTO {

    private Long equipmentId;
    private String equipmentName;
    private String laboratoryName;
    private long totalBookings;
    private double totalUsageHours;
    private long totalUsageDays;
    private double utilizationPercentage;
    private String lastUsedDate;
    private double avgUsagePerMonth;
    private double avgUsagePerWeek;
    private long approvedBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long rejectedBookings;
    private long idleDays;
    private String lastUpdated;
    private String utilizationTier; // HIGH, MEDIUM, LOW
}
