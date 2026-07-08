package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository,
                                  EquipmentRepository equipmentRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<Maintenance> getAllMaintenanceRecords() {
        return maintenanceRepository.findAll();
    }

    @Override
    public Maintenance getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
    }

    @Override
    public Maintenance createMaintenance(Maintenance maintenance) {

        Long equipmentId = maintenance.getEquipment().getId();

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);

        equipmentRepository.save(equipment);

        maintenance.setEquipment(equipment);

        maintenance.setStatus("IN_PROGRESS");

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance updateMaintenance(Long id, Maintenance updatedMaintenance) {

        Maintenance maintenance = getMaintenanceById(id);

        Long equipmentId = updatedMaintenance.getEquipment().getId();

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        maintenance.setEquipment(equipment);
        maintenance.setMaintenanceDate(updatedMaintenance.getMaintenanceDate());
        maintenance.setDescription(updatedMaintenance.getDescription());
        maintenance.setPerformedBy(updatedMaintenance.getPerformedBy());
        maintenance.setStatus(updatedMaintenance.getStatus());

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public void deleteMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenanceRepository.delete(maintenance);
    }

    @Override
    public Maintenance completeMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenance.setStatus("COMPLETED");

        Equipment equipment = maintenance.getEquipment();

        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        return maintenanceRepository.save(maintenance);
    }
}