package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.EquipmentUtilizationStatsDTO;
import com.labplatform.lab_platform_backend.dto.UtilizationDashboardDTO;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.*;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentUtilizationStatsServiceImpl implements EquipmentUtilizationStatsService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentUtilizationStatsRepository statsRepository;
    private final BookingRepository bookingRepository;
    private final UtilizationRepository utilizationRepository;
    private final SecurityUtil securityUtil;

    public EquipmentUtilizationStatsServiceImpl(
            EquipmentRepository equipmentRepository,
            EquipmentUtilizationStatsRepository statsRepository,
            BookingRepository bookingRepository,
            UtilizationRepository utilizationRepository,
            SecurityUtil securityUtil) {

        this.equipmentRepository = equipmentRepository;
        this.statsRepository = statsRepository;
        this.bookingRepository = bookingRepository;
        this.utilizationRepository = utilizationRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public void computeAndSaveAllStats() {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        for (Equipment equipment : allEquipment) {
            computeAndSaveForEquipment(equipment);
        }
    }

    @Override
    public void computeAndSaveStatsForEquipment(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        computeAndSaveForEquipment(equipment);
    }

    private void computeAndSaveForEquipment(Equipment equipment) {

        EquipmentUtilizationStats stats = statsRepository
                .findByEquipmentId(equipment.getId())
                .orElse(new EquipmentUtilizationStats());

        stats.setEquipment(equipment);

        List<Booking> allBookings = bookingRepository.findByEquipmentId(equipment.getId());

        // Count bookings by status
        long approved = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED).count();
        long completed = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
        long cancelled = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long rejected = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.REJECTED).count();

        stats.setTotalBookings(allBookings.size());
        stats.setApprovedBookings(approved);
        stats.setCompletedBookings(completed);
        stats.setCancelledBookings(cancelled);
        stats.setRejectedBookings(rejected);

        // Total usage hours from utilization records
        Double totalHours = utilizationRepository
                .getTotalUtilizationHoursByEquipmentId(equipment.getId());
        stats.setTotalUsageHours(totalHours != null ? totalHours : 0.0);

        // Total usage days from completed bookings
        long totalDays = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED
                        && b.getStartDate() != null && b.getEndDate() != null)
                .mapToLong(b -> ChronoUnit.DAYS.between(b.getStartDate(), b.getEndDate()) + 1)
                .sum();
        stats.setTotalUsageDays(totalDays);

        // Utilization percentage (based on 365 day calendar year)
        double utilizationPct = Math.min((totalDays / 365.0) * 100.0, 100.0);
        stats.setUtilizationPercentage(Math.round(utilizationPct * 100.0) / 100.0);

        // Last used date from last completed booking
        allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED && b.getEndDate() != null)
                .map(Booking::getEndDate)
                .max(LocalDate::compareTo)
                .ifPresent(stats::setLastUsedDate);

        // Idle days
        if (stats.getLastUsedDate() != null) {
            long idleDays = ChronoUnit.DAYS.between(stats.getLastUsedDate(), LocalDate.now());
            stats.setIdleDays(Math.max(0, idleDays));
        } else {
            stats.setIdleDays(0);
        }

        // Average usage per month (totalDays / 12)
        stats.setAvgUsagePerMonth(Math.round((totalDays / 12.0) * 100.0) / 100.0);

        // Average usage per week (totalDays / 52)
        stats.setAvgUsagePerWeek(Math.round((totalDays / 52.0) * 100.0) / 100.0);

        stats.setLastUpdated(java.time.LocalDateTime.now());

        statsRepository.save(stats);
    }

    @Override
    public EquipmentUtilizationStats getStatsByEquipmentId(Long equipmentId) {
        EquipmentUtilizationStats stats = statsRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new RuntimeException("Stats not found for equipment: " + equipmentId));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewEquipment(user, stats.getEquipment())) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied: Unauthorized to view stats for this equipment");
        }

        return stats;
    }

    @Override
    public List<EquipmentUtilizationStatsDTO> getAllStats() {
        User user = securityUtil.getCurrentUser();
        List<EquipmentUtilizationStats> statsList;

        if (securityUtil.isSystemAdmin(user)) {
            statsList = statsRepository.findAll();
        } else if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            statsList = statsRepository.findByEquipmentLaboratoryId(user.getLaboratory().getId());
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId == null) return List.of();
            statsList = statsRepository.findByEquipmentLaboratoryInstitutionId(instId);
        }

        return statsList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentUtilizationStatsDTO> getStatsByRanking() {
        return getAllStats().stream()
                .sorted((a, b) -> Double.compare(b.getUtilizationPercentage(), a.getUtilizationPercentage()))
                .collect(Collectors.toList());
    }

    @Override
    public UtilizationDashboardDTO getUtilizationDashboard() {

        List<EquipmentUtilizationStatsDTO> allStats = getAllStats();

        UtilizationDashboardDTO dashboard = new UtilizationDashboardDTO();
        dashboard.setTotalEquipmentCount(allStats.size());

        double avgPct = allStats.stream()
                .mapToDouble(EquipmentUtilizationStatsDTO::getUtilizationPercentage)
                .average()
                .orElse(0.0);
        dashboard.setAverageUtilizationPercentage(Math.round(avgPct * 100.0) / 100.0);

        dashboard.setHighlyUtilizedCount(allStats.stream()
                .filter(s -> s.getUtilizationPercentage() >= 70).count());

        dashboard.setMediumUtilizedCount(allStats.stream()
                .filter(s -> s.getUtilizationPercentage() >= 30
                        && s.getUtilizationPercentage() < 70).count());

        dashboard.setLowUtilizedCount(allStats.stream()
                .filter(s -> s.getUtilizationPercentage() < 30).count());

        dashboard.setIdleEquipmentCount(allStats.stream()
                .filter(s -> s.getIdleDays() > 30).count());

        double totalHours = allStats.stream()
                .mapToDouble(EquipmentUtilizationStatsDTO::getTotalUsageHours)
                .sum();
        dashboard.setTotalUsageHours(Math.round(totalHours * 100.0) / 100.0);

        // Most used — top 5 by bookings
        List<EquipmentUtilizationStatsDTO> byBookingsDesc = allStats.stream()
                .sorted((a, b) -> Long.compare(b.getTotalBookings(), a.getTotalBookings()))
                .collect(Collectors.toList());

        dashboard.setMostUsedEquipment(byBookingsDesc.stream().limit(5).collect(Collectors.toList()));

        // Least used — bottom 5 by bookings
        List<EquipmentUtilizationStatsDTO> byBookingsAsc = allStats.stream()
                .sorted(java.util.Comparator.comparingLong(EquipmentUtilizationStatsDTO::getTotalBookings))
                .collect(Collectors.toList());

        dashboard.setLeastUsedEquipment(byBookingsAsc.stream().limit(5).collect(Collectors.toList()));

        // Utilization ranking — all by %
        dashboard.setUtilizationRanking(getStatsByRanking());

        return dashboard;
    }

    public EquipmentUtilizationStatsDTO toDTO(EquipmentUtilizationStats stats) {

        EquipmentUtilizationStatsDTO dto = new EquipmentUtilizationStatsDTO();

        dto.setEquipmentId(stats.getEquipment().getId());
        dto.setEquipmentName(stats.getEquipment().getName());

        if (stats.getEquipment().getLaboratory() != null) {
            dto.setLaboratoryName(stats.getEquipment().getLaboratory().getName());
        }

        dto.setTotalBookings(stats.getTotalBookings());
        dto.setTotalUsageHours(stats.getTotalUsageHours());
        dto.setTotalUsageDays(stats.getTotalUsageDays());
        dto.setUtilizationPercentage(stats.getUtilizationPercentage());
        dto.setLastUsedDate(stats.getLastUsedDate() != null ? stats.getLastUsedDate().toString() : null);
        dto.setAvgUsagePerMonth(stats.getAvgUsagePerMonth());
        dto.setAvgUsagePerWeek(stats.getAvgUsagePerWeek());
        dto.setApprovedBookings(stats.getApprovedBookings());
        dto.setCompletedBookings(stats.getCompletedBookings());
        dto.setCancelledBookings(stats.getCancelledBookings());
        dto.setRejectedBookings(stats.getRejectedBookings());
        dto.setIdleDays(stats.getIdleDays());
        dto.setLastUpdated(stats.getLastUpdated() != null ? stats.getLastUpdated().toString() : null);

        // Utilization tier
        double pct = stats.getUtilizationPercentage();
        if (pct >= 70) {
            dto.setUtilizationTier("HIGH");
        } else if (pct >= 30) {
            dto.setUtilizationTier("MEDIUM");
        } else {
            dto.setUtilizationTier("LOW");
        }

        return dto;
    }
}
