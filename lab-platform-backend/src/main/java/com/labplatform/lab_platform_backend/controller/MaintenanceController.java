package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.service.MaintenanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LAB_TECHNICIAN','LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public List<Maintenance> getAllMaintenanceRecords() {
        return maintenanceService.getAllMaintenanceRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LAB_TECHNICIAN','LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance getMaintenanceById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public List<Maintenance> getPendingMaintenance() {
        return maintenanceService.getPendingMaintenance();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public List<Maintenance> getMyMaintenance(Authentication authentication) {
        return maintenanceService.getMyMaintenance(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance createMaintenance(@RequestBody Maintenance maintenance) {
        return maintenanceService.createMaintenance(maintenance);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance updateMaintenance(@PathVariable Long id,
                                         @RequestBody Maintenance maintenance) {
        return maintenanceService.updateMaintenance(id, maintenance);
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance startMaintenance(@PathVariable Long id) {
        return maintenanceService.startMaintenance(id);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance completeMaintenance(@PathVariable Long id) {
        return maintenanceService.completeMaintenance(id);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('LAB_MANAGER','INSTITUTION_ADMIN','SYSTEM_ADMIN')")
    public Maintenance cancelMaintenance(@PathVariable Long id) {
        return maintenanceService.cancelMaintenance(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','INSTITUTION_ADMIN')")
    public void deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
    }
}