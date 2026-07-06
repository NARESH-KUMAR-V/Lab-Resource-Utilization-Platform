package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Utilization;
import com.labplatform.lab_platform_backend.repository.UtilizationRepository;
import org.springframework.stereotype.Service;
import com.labplatform.lab_platform_backend.entity.Booking;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class UtilizationServiceImpl implements UtilizationService {

    private final UtilizationRepository utilizationRepository;

    public UtilizationServiceImpl(UtilizationRepository utilizationRepository) {
        this.utilizationRepository = utilizationRepository;
    }

    @Override
    public List<Utilization> getAllUtilizations() {
        return utilizationRepository.findAll();
    }

    @Override
    public Utilization getUtilizationById(Long id) {
        return utilizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilization record not found"));
    }

    @Override
    public Utilization createUtilization(Utilization utilization) {
        return utilizationRepository.save(utilization);
    }

    @Override
    public Utilization updateUtilization(Long id, Utilization updatedUtilization) {

        Utilization utilization = getUtilizationById(id);

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