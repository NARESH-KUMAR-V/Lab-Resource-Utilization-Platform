package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class UtilizationDashboardDTO {

    private long totalEquipmentCount;
    private double averageUtilizationPercentage;
    private long highlyUtilizedCount;   // >= 70%
    private long mediumUtilizedCount;   // 30 - 70%
    private long lowUtilizedCount;      // < 30%
    private long idleEquipmentCount;    // idleDays > 30
    private double totalUsageHours;

    private List<EquipmentUtilizationStatsDTO> mostUsedEquipment;    // top 5 by bookings
    private List<EquipmentUtilizationStatsDTO> leastUsedEquipment;   // bottom 5 by bookings
    private List<EquipmentUtilizationStatsDTO> utilizationRanking;   // all, sorted by utilization %
}
