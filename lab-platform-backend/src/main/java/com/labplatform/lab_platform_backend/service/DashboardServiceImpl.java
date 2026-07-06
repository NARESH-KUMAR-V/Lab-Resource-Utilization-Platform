package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.DashboardStatsResponse;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;

    public DashboardServiceImpl(EquipmentRepository equipmentRepository,
                                BookingRepository bookingRepository) {
        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public DashboardStatsResponse getDashboardStats(String userEmail) {

        DashboardStatsResponse stats = new DashboardStatsResponse();

        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<Booking> myBookings = bookingRepository.findByUserEmail(userEmail);

        stats.setTotalEquipment(equipmentList.size());

        long available = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE)
                .count();

        long booked = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.BOOKED)
                .count();

        long maintenance = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.UNDER_MAINTENANCE)
                .count();

        stats.setAvailableEquipment(available);
        stats.setBookedEquipment(booked);
        stats.setMaintenanceEquipment(maintenance);
        stats.setMyBookings(myBookings.size());

        return stats;
    }
}