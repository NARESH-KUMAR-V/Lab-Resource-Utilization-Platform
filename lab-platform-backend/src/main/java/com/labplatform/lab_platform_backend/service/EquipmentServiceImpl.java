package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.LaboratoryRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final SharingRequestRepository sharingRequestRepository;
    private final BookingRepository bookingRepository;
    private final MaintenanceRepository maintenanceRepository;

    public EquipmentServiceImpl(
            EquipmentRepository equipmentRepository,
            LaboratoryRepository laboratoryRepository,
            UserRepository userRepository,
            SecurityUtil securityUtil,
            @Lazy SharingRequestRepository sharingRequestRepository,
            @Lazy BookingRepository bookingRepository,
            @Lazy MaintenanceRepository maintenanceRepository) {

        this.equipmentRepository = equipmentRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.sharingRequestRepository = sharingRequestRepository;
        this.bookingRepository = bookingRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipment(String email) {
        User user = securityUtil.getCurrentUser();
        Role role = user.getRole();

        if (role == Role.SYSTEM_ADMIN) {
            return equipmentRepository.findAll();
        }

        if (role == Role.LAB_MANAGER && user.getLaboratory() != null) {
            List<Equipment> labEquipment = new ArrayList<>(
                    equipmentRepository.findByLaboratoryId(user.getLaboratory().getId())
            );
            List<Equipment> sharedEquipment = equipmentRepository.findBySharedTrue();
            for (Equipment shared : sharedEquipment) {
                if (labEquipment.stream().noneMatch(e -> e.getId().equals(shared.getId()))) {
                    labEquipment.add(shared);
                }
            }
            return labEquipment;
        }

        Long instId = securityUtil.getUserInstitutionId(user);

        if (role == Role.INSTITUTION_ADMIN) {
            if (instId == null) return List.of();
            return equipmentRepository.findByLaboratoryInstitutionId(instId);
        }

        if (instId == null) {
            return equipmentRepository.findBySharedTrue();
        }

        List<Equipment> instEquipment = new ArrayList<>(
                equipmentRepository.findByLaboratoryInstitutionId(instId)
        );
        List<Equipment> sharedEquipment = equipmentRepository.findBySharedTrue();

        for (Equipment shared : sharedEquipment) {
            if (instEquipment.stream().noneMatch(e -> e.getId().equals(shared.getId()))) {
                instEquipment.add(shared);
            }
        }

        return instEquipment;
    }

    @Override
    @Transactional(readOnly = true)
    public Equipment getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this equipment");
        }
        return equipment;
    }

    @Override
    public Equipment createEquipment(Equipment equipment) {
        User user = securityUtil.getCurrentUser();
        if (user.getRole() == Role.RESEARCHER) {
            throw new AccessDeniedException("Access denied: Researchers cannot create equipment");
        }

        if (equipment.getLaboratory() == null || equipment.getLaboratory().getId() == null) {
            throw new RuntimeException("Laboratory selection is required to create equipment.");
        }

        Laboratory lab = laboratoryRepository.findById(equipment.getLaboratory().getId())
                .orElseThrow(() -> new RuntimeException("Laboratory not found with id: " + equipment.getLaboratory().getId()));

        equipment.setLaboratory(lab);

        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You are not authorized to create equipment in this laboratory or institution");
        }

        return equipmentRepository.save(equipment);
    }

    @Override
    public Equipment updateEquipment(Long id, Equipment updatedEquipment) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You cannot update equipment belonging to another laboratory or institution");
        }

        if (updatedEquipment.getLaboratory() != null && updatedEquipment.getLaboratory().getId() != null) {
            Laboratory lab = laboratoryRepository.findById(updatedEquipment.getLaboratory().getId())
                    .orElseThrow(() -> new RuntimeException("Laboratory not found with id: " + updatedEquipment.getLaboratory().getId()));
            equipment.setLaboratory(lab);
            if (!securityUtil.canManageEquipment(user, equipment)) {
                throw new AccessDeniedException("Access denied: Cannot reassign equipment to a laboratory belonging to another institution");
            }
        }

        equipment.setName(updatedEquipment.getName());
        equipment.setCategory(updatedEquipment.getCategory());
        equipment.setSpecifications(updatedEquipment.getSpecifications());
        equipment.setDescription(updatedEquipment.getDescription());
        equipment.setImageUrl(updatedEquipment.getImageUrl());
        equipment.setStatus(updatedEquipment.getStatus());
        equipment.setShared(updatedEquipment.getShared());

        return equipmentRepository.save(equipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You cannot delete equipment belonging to another laboratory or institution");
        }

        equipmentRepository.delete(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> searchEquipment(String name) {
        return equipmentRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getSharedEquipment() {
        return equipmentRepository.findBySharedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getExternalSharedEquipment() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return equipmentRepository.findBySharedTrue();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return equipmentRepository.findBySharedTrue();
        }
        return equipmentRepository.findExternalSharedEquipment(instId);
    }

    @Override
    public Equipment shareEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You cannot share equipment belonging to another laboratory or institution");
        }

        equipment.setShared(true);
        return equipmentRepository.save(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentByLaboratory(Long laboratoryId) {
        return equipmentRepository.findByLaboratoryId(laboratoryId);
    }

    @Override
    public void recalculateEquipmentStatus(Equipment equipment) {
        if (equipment == null || equipment.getId() == null) return;

        // Preserve explicit hardware states
        if (equipment.getStatus() == EquipmentStatus.OUT_OF_SERVICE || equipment.getStatus() == EquipmentStatus.RETIRED) {
            return;
        }

        // 1. Check for active maintenance
        boolean hasActiveMaintenance = maintenanceRepository.existsByEquipmentIdAndStatusIn(
                equipment.getId(), List.of(MaintenanceStatus.PENDING, MaintenanceStatus.IN_PROGRESS)
        );
        if (hasActiveMaintenance) {
            equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);
            equipmentRepository.save(equipment);
            return;
        }

        LocalDate today = LocalDate.now();

        // 2. Check for active inter-institution sharing
        boolean isSharingActive = sharingRequestRepository.existsActiveSharingForDate(equipment.getId(), today);
        if (isSharingActive) {
            equipment.setStatus(EquipmentStatus.SHARED);
            equipmentRepository.save(equipment);
            return;
        }

        // 3. Check for active approved booking
        List<Booking> activeBookings = bookingRepository.findByEquipmentIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                equipment.getId(), BookingStatus.APPROVED, today, today
        );
        if (!activeBookings.isEmpty()) {
            equipment.setStatus(EquipmentStatus.BOOKED);
            equipmentRepository.save(equipment);
            return;
        }

        // 4. Otherwise, set to AVAILABLE
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);
    }
}