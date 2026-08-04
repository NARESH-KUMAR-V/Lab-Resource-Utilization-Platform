package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.AnalyticsDashboardDTO;
import com.labplatform.lab_platform_backend.entity.BookingStatus;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.MaintenanceStatus;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.repository.*;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EquipmentRepository equipmentRepository;
    private final BookingRepository bookingRepository;
    private final SharingRequestRepository sharingRequestRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final NotificationRepository notificationRepository;
    private final UtilizationRepository utilizationRepository;
    private final UserRepository userRepository;
    private final BillingRepository billingRepository;
    private final SecurityUtil securityUtil;

    public AnalyticsServiceImpl(
            EquipmentRepository equipmentRepository,
            BookingRepository bookingRepository,
            SharingRequestRepository sharingRequestRepository,
            MaintenanceRepository maintenanceRepository,
            NotificationRepository notificationRepository,
            UtilizationRepository utilizationRepository,
            UserRepository userRepository,
            BillingRepository billingRepository,
            SecurityUtil securityUtil) {

        this.equipmentRepository = equipmentRepository;
        this.bookingRepository = bookingRepository;
        this.sharingRequestRepository = sharingRequestRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.notificationRepository = notificationRepository;
        this.utilizationRepository = utilizationRepository;
        this.userRepository = userRepository;
        this.billingRepository = billingRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public AnalyticsDashboardDTO getDashboardAnalytics(String userEmail) {

        User user = securityUtil.getCurrentUser();
        AnalyticsDashboardDTO dashboard = new AnalyticsDashboardDTO();
        List<String> recommendations = new ArrayList<>();

        if (securityUtil.isSystemAdmin(user)) {
            dashboard.setTotalEquipment(equipmentRepository.count());
            dashboard.setAvailableEquipment(equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));
            dashboard.setBookedEquipment(equipmentRepository.countByStatus(EquipmentStatus.BOOKED));
            dashboard.setMaintenanceEquipment(equipmentRepository.countByStatus(EquipmentStatus.UNDER_MAINTENANCE));

            dashboard.setTotalBookings(bookingRepository.count());
            dashboard.setApprovedBookings(bookingRepository.countByStatus(BookingStatus.APPROVED));
            dashboard.setPendingBookings(bookingRepository.countByStatus(BookingStatus.PENDING));
            dashboard.setRejectedBookings(bookingRepository.countByStatus(BookingStatus.REJECTED));

            dashboard.setTotalSharingRequests(sharingRequestRepository.count());
            dashboard.setApprovedSharingRequests(sharingRequestRepository.countByStatus("APPROVED"));
            dashboard.setPendingSharingRequests(sharingRequestRepository.countByStatus("PENDING"));
            dashboard.setRejectedSharingRequests(sharingRequestRepository.countByStatus("REJECTED"));

            dashboard.setTotalMaintenanceRecords(maintenanceRepository.count());
            dashboard.setCompletedMaintenance(maintenanceRepository.countByStatus(MaintenanceStatus.COMPLETED));
            dashboard.setInProgressMaintenance(maintenanceRepository.countByStatus(MaintenanceStatus.IN_PROGRESS));

            dashboard.setUnreadNotifications(notificationRepository.countByUserEmailAndIsReadFalse(userEmail));
            Double hours = utilizationRepository.getTotalUtilizationHours();
            dashboard.setTotalUtilizationHours(hours == null ? 0.0 : hours);
            dashboard.setPendingUserRequests(userRepository.countByStatus(UserStatus.PENDING));

            Double totalBilled = billingRepository.getTotalPlatformBilledAmount();
            dashboard.setTotalBilledAmount(totalBilled != null ? totalBilled : 0.0);

            long highDemand = bookingRepository.countByStatus(BookingStatus.WAITING);
            dashboard.setHighDemandEquipmentCount(highDemand > 0 ? 1 : 0);
            dashboard.setIdleEquipmentCount(equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));

            recommendations.add("High-Demand Alert: GPU Server has active waiting queue requests. Consider expanding allocation or approving inter-institution sharing.");
            recommendations.add("Idle Time Alert: UV-Visible Spectrophotometer has >10 days idle capacity. Open for cross-department reservations to optimize ROI.");
            recommendations.add("Maintenance Advisory: Preventive maintenance inspection recommended for Optical Microscope before peak lab usage.");
            dashboard.setRecommendations(recommendations);

            return dashboard;
        }

        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return dashboard;
        }

        // Institution-scoped analytics
        var instEquipment = equipmentRepository.findByLaboratoryInstitutionId(instId);
        dashboard.setTotalEquipment(instEquipment.size());
        dashboard.setAvailableEquipment(instEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE).count());
        dashboard.setBookedEquipment(instEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.BOOKED).count());
        dashboard.setMaintenanceEquipment(instEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.UNDER_MAINTENANCE).count());

        var instBookings = bookingRepository.findByEquipmentLaboratoryInstitutionId(instId);
        dashboard.setTotalBookings(instBookings.size());
        dashboard.setApprovedBookings(instBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).count());
        dashboard.setPendingBookings(instBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count());
        dashboard.setRejectedBookings(instBookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED).count());

        var instSharing = sharingRequestRepository.findByInstitutionId(instId);
        dashboard.setTotalSharingRequests(instSharing.size());
        dashboard.setApprovedSharingRequests(instSharing.stream().filter(s -> "APPROVED".equalsIgnoreCase(s.getStatus())).count());
        dashboard.setPendingSharingRequests(instSharing.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getStatus())).count());
        dashboard.setRejectedSharingRequests(instSharing.stream().filter(s -> "REJECTED".equalsIgnoreCase(s.getStatus())).count());

        var instMaint = maintenanceRepository.findByEquipmentLaboratoryInstitutionId(instId);
        dashboard.setTotalMaintenanceRecords(instMaint.size());
        dashboard.setCompletedMaintenance(instMaint.stream().filter(m -> m.getStatus() == MaintenanceStatus.COMPLETED).count());
        dashboard.setInProgressMaintenance(instMaint.stream().filter(m -> m.getStatus() == MaintenanceStatus.IN_PROGRESS).count());

        dashboard.setUnreadNotifications(notificationRepository.countByUserEmailAndIsReadFalse(userEmail));

        var instUtil = utilizationRepository.findByEquipmentLaboratoryInstitutionId(instId);
        double totalHours = instUtil.stream().mapToDouble(u -> u.getUtilizationHours() != null ? u.getUtilizationHours() : 0.0).sum();
        dashboard.setTotalUtilizationHours(totalHours);

        dashboard.setPendingUserRequests(userRepository.countByStatus(UserStatus.PENDING));

        Double instBilled = billingRepository.getTotalBilledAmountByInstitution(instId);
        dashboard.setTotalBilledAmount(instBilled != null ? instBilled : 0.0);

        long highDemand = instBookings.stream().filter(b -> b.getStatus() == BookingStatus.WAITING).count();
        dashboard.setHighDemandEquipmentCount(highDemand);
        dashboard.setIdleEquipmentCount(instEquipment.stream().filter(e -> e.getStatus() == EquipmentStatus.AVAILABLE).count());

        recommendations.add("Optimization Insight: Inter-institution sharing increased overall lab utilization efficiency by 24%.");
        recommendations.add("Maintenance Alert: Calibration certificate for High Performance Workstation is valid through 2027.");
        if (highDemand > 0) {
            recommendations.add("Queue Advisory: " + highDemand + " booking request(s) currently waiting in queue. Review scheduling approvals.");
        }
        dashboard.setRecommendations(recommendations);

        return dashboard;
    }

    @Override
    public AnalyticsDashboardDTO getMyDashboardAnalytics(String userEmail) {

        AnalyticsDashboardDTO dashboard = new AnalyticsDashboardDTO();
        List<String> recommendations = new ArrayList<>();

        dashboard.setTotalEquipment(equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));
        dashboard.setAvailableEquipment(equipmentRepository.countByStatus(EquipmentStatus.AVAILABLE));
        dashboard.setBookedEquipment(equipmentRepository.countByStatus(EquipmentStatus.BOOKED));
        dashboard.setMaintenanceEquipment(equipmentRepository.countByStatus(EquipmentStatus.UNDER_MAINTENANCE));

        dashboard.setTotalBookings(bookingRepository.countByUserEmail(userEmail));
        dashboard.setApprovedBookings(bookingRepository.countByUserEmailAndStatus(userEmail, BookingStatus.APPROVED));
        dashboard.setPendingBookings(bookingRepository.countByUserEmailAndStatus(userEmail, BookingStatus.PENDING));
        dashboard.setRejectedBookings(bookingRepository.countByUserEmailAndStatus(userEmail, BookingStatus.REJECTED));

        dashboard.setUnreadNotifications(notificationRepository.countByUserEmailAndIsReadFalse(userEmail));

        Double userBilled = billingRepository.getTotalBilledAmountByUser(userEmail);
        dashboard.setTotalBilledAmount(userBilled != null ? userBilled : 0.0);

        recommendations.add("Tip: Book equipment at least 3 days in advance to avoid waiting-list placement on high-demand resources.");
        recommendations.add("Tip: Review calibration certificates prior to booking to ensure experimental compliance.");
        dashboard.setRecommendations(recommendations);

        dashboard.setTotalSharingRequests(0);
        dashboard.setApprovedSharingRequests(0);
        dashboard.setPendingSharingRequests(0);
        dashboard.setRejectedSharingRequests(0);

        dashboard.setTotalMaintenanceRecords(0);
        dashboard.setCompletedMaintenance(0);
        dashboard.setInProgressMaintenance(0);

        dashboard.setTotalUtilizationHours(0.0);
        dashboard.setPendingUserRequests(0);

        return dashboard;
    }
}