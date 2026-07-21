package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;
import com.labplatform.lab_platform_backend.entity.BookingStatus;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.MaintenanceStatus;
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
    public AnalyticsDashboardDTO getDashboardAnalytics(String userEmail) {

        AnalyticsDashboardDTO dashboard = new AnalyticsDashboardDTO();

        // Equipment
        dashboard.setTotalEquipment(equipmentRepository.count());

        dashboard.setAvailableEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));

        dashboard.setBookedEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.BOOKED));

        dashboard.setMaintenanceEquipment(
                equipmentRepository.countByStatus(
                        EquipmentStatus.UNDER_MAINTENANCE));

        // Booking
        dashboard.setTotalBookings(bookingRepository.count());

        dashboard.setApprovedBookings(
                bookingRepository.countByStatus(BookingStatus.APPROVED));

        dashboard.setPendingBookings(
                bookingRepository.countByStatus(BookingStatus.PENDING));

        dashboard.setRejectedBookings(
                bookingRepository.countByStatus(BookingStatus.REJECTED));

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
                maintenanceRepository.countByStatus(
                        MaintenanceStatus.COMPLETED));

        dashboard.setInProgressMaintenance(
                maintenanceRepository.countByStatus(
                        MaintenanceStatus.IN_PROGRESS));

        // Notifications
        dashboard.setUnreadNotifications(
                notificationRepository.countByUserEmailAndIsReadFalse(userEmail));

        // Utilization
        Double hours = utilizationRepository.getTotalUtilizationHours();

        dashboard.setTotalUtilizationHours(
                hours == null ? 0.0 : hours);

        return dashboard;
    }

    @Override
    public AnalyticsDashboardDTO getMyDashboardAnalytics(String userEmail) {

        AnalyticsDashboardDTO dashboard = new AnalyticsDashboardDTO();

        // Equipment
        dashboard.setTotalEquipment(equipmentRepository.count());

        dashboard.setAvailableEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));

        dashboard.setBookedEquipment(
                equipmentRepository.countByStatus(EquipmentStatus.BOOKED));

        dashboard.setMaintenanceEquipment(
                equipmentRepository.countByStatus(
                        EquipmentStatus.UNDER_MAINTENANCE));

        // My Bookings
        dashboard.setTotalBookings(
                bookingRepository.countByUserEmail(userEmail));

        dashboard.setApprovedBookings(
                bookingRepository.countByUserEmailAndStatus(
                        userEmail,
                        BookingStatus.APPROVED));

        dashboard.setPendingBookings(
                bookingRepository.countByUserEmailAndStatus(
                        userEmail,
                        BookingStatus.PENDING));

        dashboard.setRejectedBookings(
                bookingRepository.countByUserEmailAndStatus(
                        userEmail,
                        BookingStatus.REJECTED));

        // Notifications
        dashboard.setUnreadNotifications(
                notificationRepository.countByUserEmailAndIsReadFalse(userEmail));

        // Researchers don't need organization-wide values
        dashboard.setTotalSharingRequests(0);
        dashboard.setApprovedSharingRequests(0);
        dashboard.setPendingSharingRequests(0);
        dashboard.setRejectedSharingRequests(0);

        dashboard.setTotalMaintenanceRecords(0);
        dashboard.setCompletedMaintenance(0);
        dashboard.setInProgressMaintenance(0);

        dashboard.setTotalUtilizationHours(0.0);

        return dashboard;
    }
}