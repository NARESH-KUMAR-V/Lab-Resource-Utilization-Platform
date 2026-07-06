package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import com.labplatform.lab_platform_backend.repository.NotificationRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UtilizationRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;
    private final SharingRequestRepository sharingRequestRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final NotificationRepository notificationRepository;
    private final UtilizationRepository utilizationRepository;

    public AnalyticsServiceImpl(
            EquipmentRepository equipmentRepository,
            BookingRepository bookingRepository,
            SharingRequestRepository sharingRequestRepository,
            MaintenanceRepository maintenanceRepository,
            NotificationRepository notificationRepository,
            UtilizationRepository utilizationRepository) {

        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
        this.sharingRequestRepository = sharingRequestRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.notificationRepository = notificationRepository;
        this.utilizationRepository = utilizationRepository;
    }

    @Override
    public AnalyticsDashboardDTO getDashboardAnalytics() {

        AnalyticsDashboardDTO dashboard = new AnalyticsDashboardDTO();

        // Equipment
        dashboard.setTotalEquipment(equipmentRepository.count());
        dashboard.setAvailableEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));
        dashboard.setBookedEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.BOOKED));
        dashboard.setMaintenanceEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.UNDER_MAINTENANCE));

        // Booking
        dashboard.setTotalBookings(bookingRepository.count());
        dashboard.setApprovedBookings(
                bookingRepository.countByStatus("APPROVED"));
        dashboard.setPendingBookings(
                bookingRepository.countByStatus("PENDING"));
        dashboard.setRejectedBookings(
                bookingRepository.countByStatus("REJECTED"));

        // Sharing
        dashboard.setTotalSharingRequests(sharingRequestRepository.count());
        dashboard.setApprovedSharingRequests(
                sharingRequestRepository.countByStatus("APPROVED"));
        dashboard.setPendingSharingRequests(
                sharingRequestRepository.countByStatus("PENDING"));
        dashboard.setRejectedSharingRequests(
                sharingRequestRepository.countByStatus("REJECTED"));

        // Maintenance
        dashboard.setTotalMaintenanceRecords(maintenanceRepository.count());
        dashboard.setCompletedMaintenance(
                maintenanceRepository.countByStatus("COMPLETED"));
        dashboard.setInProgressMaintenance(
                maintenanceRepository.countByStatus("IN_PROGRESS"));

        // Notifications
        dashboard.setUnreadNotifications(
                notificationRepository.countByIsReadFalse());

        // Utilization
        dashboard.setTotalUtilizationHours(
                utilizationRepository.getTotalUtilizationHours());

        return dashboard;
    }
}