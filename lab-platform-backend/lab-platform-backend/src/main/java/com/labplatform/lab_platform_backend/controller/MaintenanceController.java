package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.service.MaintenanceService;
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
    public List<Maintenance> getAllMaintenanceRecords() {
        return maintenanceService.getAllMaintenanceRecords();
    }

    @GetMapping("/{id}")
    public Maintenance getMaintenanceById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id);
    }

    @PostMapping
    public Maintenance createMaintenance(@RequestBody Maintenance maintenance) {
        return maintenanceService.createMaintenance(maintenance);
    }

    @PutMapping("/{id}")
    public Maintenance updateMaintenance(@PathVariable Long id,
                                         @RequestBody Maintenance maintenance) {
        return maintenanceService.updateMaintenance(id, maintenance);
    }

    @PutMapping("/{id}/complete")
    public Maintenance completeMaintenance(@PathVariable Long id) {

        return maintenanceService.completeMaintenance(id);

    }

    @DeleteMapping("/{id}")
    public void deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
    }
}