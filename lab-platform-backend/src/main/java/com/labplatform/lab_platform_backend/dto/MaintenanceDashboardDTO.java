package com.labplatform.lab_platform_backend.dto;

import com.labplatform.lab_platform_backend.entity.EquipmentCertificate;
import com.labplatform.lab_platform_backend.entity.Maintenance;
import lombok.Data;

import java.util.List;

@Data
public class MaintenanceDashboardDTO {

    private long upcomingMaintenanceCount;
    private long overdueMaintenanceCount;
    private long certificatesExpiringCount;
    private long expiredCertificatesCount;
    private double totalMaintenanceCost;
    private long totalMaintenanceCount;
    private long completedMaintenanceCount;
    private long pendingMaintenanceCount;
    private long inProgressMaintenanceCount;

    private List<Maintenance> upcomingMaintenance;
    private List<Maintenance> overdueMaintenance;
    private List<EquipmentCertificate> expiringCertificates;
}
