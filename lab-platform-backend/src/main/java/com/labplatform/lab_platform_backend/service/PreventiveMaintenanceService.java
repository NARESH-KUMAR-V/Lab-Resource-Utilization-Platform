package com.labplatform.lab_platform_backend.service;

public interface PreventiveMaintenanceService {

    void generateMaintenanceSchedules();

    void checkAndNotifyMaintenanceDue();
}
