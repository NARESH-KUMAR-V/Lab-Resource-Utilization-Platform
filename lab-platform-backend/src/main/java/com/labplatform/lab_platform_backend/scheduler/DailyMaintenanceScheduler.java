package com.labplatform.lab_platform_backend.scheduler;

import com.labplatform.lab_platform_backend.service.EquipmentCertificateService;
import com.labplatform.lab_platform_backend.service.EquipmentUtilizationStatsService;
import com.labplatform.lab_platform_backend.service.PreventiveMaintenanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyMaintenanceScheduler {

    private final EquipmentUtilizationStatsService utilizationStatsService;
    private final EquipmentCertificateService certificateService;
    private final PreventiveMaintenanceService preventiveMaintenanceService;

    public DailyMaintenanceScheduler(
            EquipmentUtilizationStatsService utilizationStatsService,
            EquipmentCertificateService certificateService,
            PreventiveMaintenanceService preventiveMaintenanceService) {

        this.utilizationStatsService = utilizationStatsService;
        this.certificateService = certificateService;
        this.preventiveMaintenanceService = preventiveMaintenanceService;
    }

    /**
     * Runs every day at 1:00 AM.
     *
     * Step 1: Recompute EquipmentUtilizationStats for all equipment.
     * Step 2: Update CertificateStatus for all certificates.
     * Step 3: Send notifications for expiring/expired certificates.
     * Step 4: Auto-generate preventive maintenance work orders.
     * Step 5: Notify managers about due and overdue maintenance.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyJobs() {

        System.out.println("[Scheduler] Starting daily maintenance jobs...");

        // Step 1 — Recompute utilization analytics
        utilizationStatsService.computeAndSaveAllStats();
        System.out.println("[Scheduler] Step 1: Utilization stats updated.");

        // Step 2 — Refresh certificate statuses
        certificateService.updateCertificateStatuses();
        System.out.println("[Scheduler] Step 2: Certificate statuses refreshed.");

        // Step 3 — Notify about expiring and expired certificates
        certificateService.checkAndNotifyExpiringCertificates();
        System.out.println("[Scheduler] Step 3: Certificate expiry notifications sent.");

        // Step 4 — Generate preventive maintenance work orders
        preventiveMaintenanceService.generateMaintenanceSchedules();
        System.out.println("[Scheduler] Step 4: Preventive maintenance schedules generated.");

        // Step 5 — Notify managers about due/overdue maintenance
        preventiveMaintenanceService.checkAndNotifyMaintenanceDue();
        System.out.println("[Scheduler] Step 5: Maintenance due notifications sent.");

        System.out.println("[Scheduler] Daily jobs completed.");
    }
}
