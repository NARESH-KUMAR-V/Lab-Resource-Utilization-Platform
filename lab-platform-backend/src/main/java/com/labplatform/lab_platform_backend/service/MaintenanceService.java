package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Maintenance;

import java.util.List;

public interface MaintenanceService {

    List<Maintenance> getAllMaintenanceRecords();

    Maintenance getMaintenanceById(Long id);

    Maintenance createMaintenance(Maintenance maintenance);

    Maintenance updateMaintenance(Long id, Maintenance maintenance);

    Maintenance startMaintenance(Long id);

    Maintenance completeMaintenance(Long id);

    Maintenance cancelMaintenance(Long id);

    List<Maintenance> getPendingMaintenance();

    List<Maintenance> getMyMaintenance(String technicianEmail);

    void deleteMaintenance(Long id);
}