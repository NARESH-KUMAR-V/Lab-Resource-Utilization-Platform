package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.EquipmentUtilizationStatsDTO;
import com.labplatform.lab_platform_backend.dto.UtilizationDashboardDTO;
import com.labplatform.lab_platform_backend.entity.EquipmentUtilizationStats;
import com.labplatform.lab_platform_backend.service.EquipmentUtilizationStatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilization/stats")
public class EquipmentUtilizationStatsController {

    private final EquipmentUtilizationStatsService statsService;

    public EquipmentUtilizationStatsController(EquipmentUtilizationStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentUtilizationStatsDTO> getAllStats() {
        return statsService.getAllStats();
    }

    @GetMapping("/{equipmentId}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public EquipmentUtilizationStats getStatsByEquipment(@PathVariable Long equipmentId) {
        return statsService.getStatsByEquipmentId(equipmentId);
    }

    @GetMapping("/ranking")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentUtilizationStatsDTO> getStatsByRanking() {
        return statsService.getStatsByRanking();
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public UtilizationDashboardDTO getUtilizationDashboard() {
        return statsService.getUtilizationDashboard();
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public String refreshAllStats() {
        statsService.computeAndSaveAllStats();
        return "Utilization statistics refreshed successfully.";
    }

    @PostMapping("/refresh/{equipmentId}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public String refreshStatsForEquipment(@PathVariable Long equipmentId) {
        statsService.computeAndSaveStatsForEquipment(equipmentId);
        return "Utilization statistics refreshed for equipment " + equipmentId;
    }
}
