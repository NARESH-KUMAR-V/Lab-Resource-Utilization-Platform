package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.entity.MaintenanceStatus;
import com.labplatform.lab_platform_backend.entity.Role;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public MaintenanceServiceImpl(
            MaintenanceRepository maintenanceRepository,
            EquipmentRepository equipmentRepository,
            NotificationService notificationService,
            UserRepository userRepository) {

        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
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

        maintenance.setEquipment(equipment);
        maintenance.setStatus(MaintenanceStatus.PENDING);

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance updateMaintenance(Long id, Maintenance updatedMaintenance) {

        Maintenance maintenance = getMaintenanceById(id);

        Equipment equipment = equipmentRepository.findById(
                        updatedMaintenance.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        maintenance.setEquipment(equipment);
        maintenance.setMaintenanceDate(updatedMaintenance.getMaintenanceDate());
        maintenance.setDescription(updatedMaintenance.getDescription());
        maintenance.setTechnician(updatedMaintenance.getTechnician());

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance startMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenance.setStatus(MaintenanceStatus.IN_PROGRESS);

        Equipment equipment = maintenance.getEquipment();
        equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);

        equipmentRepository.save(equipment);

        if (maintenance.getTechnician() != null) {
            notificationService.createNotification(
                    maintenance.getTechnician(),
                    "You have been assigned maintenance for "
                            + equipment.getName()
            );
        }

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance completeMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenance.setStatus(MaintenanceStatus.COMPLETED);

        Equipment equipment = maintenance.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        List<User> managers = userRepository.findByRole(Role.LAB_MANAGER);

        for (User manager : managers) {

            notificationService.createNotification(
                    manager,
                    "Maintenance completed for "
                            + equipment.getName()
                            + ". Equipment is now AVAILABLE."
            );
        }

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance cancelMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenance.setStatus(MaintenanceStatus.CANCELLED);

        Equipment equipment = maintenance.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        return maintenanceRepository.save(maintenance);
    }

    @Override
    public List<Maintenance> getPendingMaintenance() {
        return maintenanceRepository.findByStatus(MaintenanceStatus.PENDING);
    }

    @Override
    public List<Maintenance> getMyMaintenance(String technicianEmail) {
        return maintenanceRepository.findByTechnicianEmail(technicianEmail);
    }

    @Override
    public void deleteMaintenance(Long id) {

        Maintenance maintenance = getMaintenanceById(id);

        maintenanceRepository.delete(maintenance);
    }
}