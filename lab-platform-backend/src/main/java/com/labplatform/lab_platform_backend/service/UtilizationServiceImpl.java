package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.Utilization;
import com.labplatform.lab_platform_backend.repository.UtilizationRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtilizationServiceImpl implements UtilizationService {

    private final UtilizationRepository utilizationRepository;
    private final SecurityUtil securityUtil;

    public UtilizationServiceImpl(
            UtilizationRepository utilizationRepository,
            SecurityUtil securityUtil) {

        this.utilizationRepository = utilizationRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public List<Utilization> getAllUtilizations() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return utilizationRepository.findAll();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) return List.of();
        return utilizationRepository.findByEquipmentLaboratoryInstitutionId(instId);
    }

    @Override
    public Utilization getUtilizationById(Long id) {
        Utilization utilization = utilizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilization record not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        boolean isUser = utilization.getUser() != null && utilization.getUser().getId().equals(user.getId());
        boolean isSameInst = securityUtil.isSameInstitution(user, utilization.getEquipment());

        if (!securityUtil.isSystemAdmin(user) && !isUser && !isSameInst) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this utilization record");
        }

        return utilization;
    }

    @Override
    public Utilization createUtilization(Utilization utilization) {
        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, utilization.getEquipment())) {
            throw new AccessDeniedException("Access denied: You cannot create utilization records for equipment belonging to another institution");
        }
        return utilizationRepository.save(utilization);
    }

    @Override
    public Utilization updateUtilization(Long id, Utilization updatedUtilization) {

        Utilization utilization = getUtilizationById(id);

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, utilization.getEquipment())) {
            throw new AccessDeniedException("Access denied: You cannot update utilization records belonging to another institution");
        }

        utilization.setEquipment(updatedUtilization.getEquipment());
        utilization.setUser(updatedUtilization.getUser());
        utilization.setStartTime(updatedUtilization.getStartTime());
        utilization.setEndTime(updatedUtilization.getEndTime());
        utilization.setUtilizationHours(updatedUtilization.getUtilizationHours());

        return utilizationRepository.save(utilization);
    }

    @Override
    public void deleteUtilization(Long id) {
        Utilization utilization = getUtilizationById(id);

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, utilization.getEquipment())) {
            throw new AccessDeniedException("Access denied: You cannot delete utilization records belonging to another institution");
        }

        utilizationRepository.delete(utilization);
    }

    @Override
    public Utilization createFromBooking(Booking booking) {

        Utilization utilization = new Utilization();

        utilization.setEquipment(booking.getEquipment());
        utilization.setUser(booking.getUser());
        utilization.setStartTime(LocalDateTime.now());
        utilization.setEndTime(null);
        utilization.setUtilizationHours(0.0);

        return utilizationRepository.save(utilization);
    }

    @Override
    public void completeUtilization(Booking booking) {

        Utilization utilization = utilizationRepository
                .findByEquipmentIdAndUserIdAndEndTimeIsNull(
                        booking.getEquipment().getId(),
                        booking.getUser().getId()
                )
                .orElseThrow(() -> new RuntimeException("Active utilization record not found"));

        utilization.setEndTime(LocalDateTime.now());

        long minutes = Duration.between(
                utilization.getStartTime(),
                utilization.getEndTime()
        ).toMinutes();

        utilization.setUtilizationHours(minutes / 60.0);

        utilizationRepository.save(utilization);
    }
}