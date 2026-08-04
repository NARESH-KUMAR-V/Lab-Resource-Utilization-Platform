package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.BillingRepository;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SharingRequestServiceImpl implements SharingRequestService {

    private final SharingRequestRepository sharingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;
    private final BookingRepository bookingRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final EquipmentService equipmentService;
    private final BillingService billingService;
    private final BillingRepository billingRepository;

    public SharingRequestServiceImpl(
            SharingRequestRepository sharingRequestRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            SecurityUtil securityUtil,
            BookingRepository bookingRepository,
            MaintenanceRepository maintenanceRepository,
            EquipmentService equipmentService,
            @Lazy BillingService billingService,
            BillingRepository billingRepository) {

        this.sharingRequestRepository = sharingRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
        this.bookingRepository = bookingRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentService = equipmentService;
        this.billingService = billingService;
        this.billingRepository = billingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharingRequest> getAllRequests() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return sharingRequestRepository.findAll();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) return List.of();
        return sharingRequestRepository.findByInstitutionId(instId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharingRequest> getIncomingRequests() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return sharingRequestRepository.findAll();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) return List.of();
        return sharingRequestRepository.findIncomingRequestsByInstitutionId(instId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharingRequest> getOutgoingRequests() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return sharingRequestRepository.findAll();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        return sharingRequestRepository.findOutgoingRequestsByUserOrInstitution(user.getId(), instId);
    }

    @Override
    @Transactional(readOnly = true)
    public SharingRequest getRequestById(Long id) {
        SharingRequest request = sharingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sharing request not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        boolean isRequester = request.getRequester() != null && request.getRequester().getId().equals(user.getId());
        boolean isOwnerInst = securityUtil.canManageEquipment(user, request.getEquipment());

        if (!securityUtil.isSystemAdmin(user) && !isRequester && !isOwnerInst) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this sharing request");
        }

        return request;
    }

    @Override
    public SharingRequest createRequest(SharingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getEquipment() == null || request.getEquipment().getId() == null) {
            throw new RuntimeException("Equipment asset selection is required for inter-institution sharing.");
        }

        Equipment equipment = equipmentRepository.findById(request.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RuntimeException("Sharing start date and end date are required.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Sharing end date cannot be before start date.");
        }

        if (request.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot request equipment sharing for past dates.");
        }

        if (!Boolean.TRUE.equals(equipment.getShared())) {
            throw new AccessDeniedException("Access denied: Selected equipment is private and not enabled for inter-institution sharing.");
        }

        Long userInstId = securityUtil.getUserInstitutionId(user);
        Long eqInstId = (equipment.getLaboratory() != null && equipment.getLaboratory().getInstitution() != null)
                ? equipment.getLaboratory().getInstitution().getId() : null;

        if (userInstId != null && userInstId.equals(eqInstId)) {
            throw new AccessDeniedException("Access denied: You cannot create an inter-institution sharing request for equipment owned by your own institution.");
        }

        // Validate date conflict during creation
        List<SharingRequest> overlappingSharing = sharingRequestRepository.findOverlappingSharingRequests(
                equipment.getId(), request.getStartDate(), request.getEndDate()
        );
        if (!overlappingSharing.isEmpty()) {
            SharingRequest conflict = overlappingSharing.get(0);
            throw new RuntimeException("The requested sharing period (" + request.getStartDate() + " to " + request.getEndDate() + ") collides with an existing approved sharing request (" + conflict.getStartDate() + " to " + conflict.getEndDate() + "). These dates are already reserved.");
        }

        List<Booking> overlappingBookings = bookingRepository.findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                equipment.getId(), List.of(BookingStatus.APPROVED), request.getEndDate(), request.getStartDate()
        );
        if (!overlappingBookings.isEmpty()) {
            Booking conflict = overlappingBookings.get(0);
            throw new RuntimeException("The requested sharing period (" + request.getStartDate() + " to " + request.getEndDate() + ") collides with an approved booking (" + conflict.getStartDate() + " to " + conflict.getEndDate() + "). These dates are already booked.");
        }

        // Authoritative backend cost calculation (Inclusive days, 10% fee)
        long days = request.getStartDate().until(request.getEndDate()).getDays() + 1;
        double costPerDay = equipment.getCostPerDay() != null ? equipment.getCostPerDay() : 2000.0;
        double baseCost = days * costPerDay;
        double interInstFee = Math.round(baseCost * 0.10 * 100.0) / 100.0;
        double totalCost = baseCost + interInstFee;

        request.setRequester(user);
        request.setEquipment(equipment);
        request.setStartDate(request.getStartDate());
        request.setEndDate(request.getEndDate());
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");
        request.setEstimatedCost(baseCost);
        request.setInterInstitutionFee(interInstFee);
        request.setTotalAmount(totalCost);

        String instName = user.getInstitution() != null ? user.getInstitution().getName()
                : (user.getLaboratory() != null && user.getLaboratory().getInstitution() != null
                ? user.getLaboratory().getInstitution().getName() : "External Institution");
        request.setRequestingInstitution(instName);

        SharingRequest savedRequest = sharingRequestRepository.save(request);

        // Notify System Admins of new sharing request audit event
        notificationService.notifySystemAdmins(
                "New Inter-Institution Sharing Request #" + savedRequest.getId() + " submitted for " + equipment.getName() + " by " + instName + "."
        );

        return savedRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharingRequest> getMyRequests(String userEmail) {
        return sharingRequestRepository.findByRequesterEmail(userEmail);
    }

    @Override
    public SharingRequest approveRequest(Long id) {
        SharingRequest request = getRequestById(id);
        User currentUser = securityUtil.getCurrentUser();

        if (!securityUtil.isSystemAdmin(currentUser) && !securityUtil.canManageEquipment(currentUser, request.getEquipment())) {
            throw new AccessDeniedException("Access denied: Only administrators of the equipment owning institution can approve sharing requests.");
        }

        Equipment equipment = request.getEquipment();

        List<SharingRequest> overlappingSharing = sharingRequestRepository.findOverlappingSharingRequests(
                equipment.getId(), request.getStartDate(), request.getEndDate()
        );
        overlappingSharing.removeIf(s -> s.getId().equals(request.getId()));

        if (!overlappingSharing.isEmpty()) {
            throw new RuntimeException("Cannot approve sharing request: Requested sharing period (" 
                    + request.getStartDate() + " to " + request.getEndDate() 
                    + ") overlaps with an existing approved sharing request.");
        }

        List<Booking> overlappingBookings = bookingRepository.findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                equipment.getId(), List.of(BookingStatus.APPROVED), request.getEndDate(), request.getStartDate()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("Cannot approve sharing request: Requested sharing period (" 
                    + request.getStartDate() + " to " + request.getEndDate() 
                    + ") overlaps with an existing approved booking.");
        }

        List<Maintenance> maintenanceList = maintenanceRepository.findByEquipmentId(equipment.getId());
        boolean maintenanceOverlap = maintenanceList.stream().anyMatch(m ->
                (m.getStatus() == MaintenanceStatus.PENDING || m.getStatus() == MaintenanceStatus.IN_PROGRESS) &&
                m.getMaintenanceDate() != null &&
                !m.getMaintenanceDate().isBefore(request.getStartDate()) &&
                !m.getMaintenanceDate().isAfter(request.getEndDate())
        );

        if (maintenanceOverlap) {
            throw new RuntimeException("Cannot approve sharing request: Equipment has scheduled maintenance during the requested sharing period (" 
                    + request.getStartDate() + " to " + request.getEndDate() + ").");
        }

        LocalDate today = LocalDate.now();

        if (!request.getStartDate().isAfter(today) && !request.getEndDate().isBefore(today)) {
            request.setStatus("ACTIVE");
            equipment.setStatus(EquipmentStatus.SHARED);
            equipmentRepository.save(equipment);
        } else {
            request.setStatus("APPROVED");
        }

        SharingRequest savedRequest = sharingRequestRepository.save(request);

        // Generate dummy billing record (GENERATED)
        billingService.generateBillingFromSharingRequest(savedRequest);

        String owningInst = (equipment.getLaboratory() != null && equipment.getLaboratory().getInstitution() != null)
                ? equipment.getLaboratory().getInstitution().getName() : "Owning Institution";

        String formattedCost = "₹" + String.format("%,.2f", savedRequest.getTotalAmount() != null ? savedRequest.getTotalAmount() : 0.0);

        notificationService.createNotification(
                request.getRequester(),
                "Your sharing request for "
                        + equipment.getName()
                        + " from " + owningInst + " has been approved for "
                        + request.getStartDate() + " – " + request.getEndDate()
                        + ". Estimated sharing cost: " + formattedCost + "."
        );

        notificationService.notifySystemAdmins(
                "Inter-Institution Sharing Request #" + savedRequest.getId() + " for " + equipment.getName() + " was APPROVED."
        );

        return savedRequest;
    }

    @Override
    public SharingRequest rejectRequest(Long id) {
        SharingRequest request = getRequestById(id);
        User currentUser = securityUtil.getCurrentUser();

        if (!securityUtil.isSystemAdmin(currentUser) && !securityUtil.canManageEquipment(currentUser, request.getEquipment())) {
            throw new AccessDeniedException("Access denied: Only administrators of the equipment owning institution can reject sharing requests.");
        }

        request.setStatus("REJECTED");

        String owningInst = (request.getEquipment().getLaboratory() != null && request.getEquipment().getLaboratory().getInstitution() != null)
                ? request.getEquipment().getLaboratory().getInstitution().getName() : "Owning Institution";

        notificationService.createNotification(
                request.getRequester(),
                "Your sharing request for "
                        + request.getEquipment().getName()
                        + " from " + owningInst + " has been rejected."
        );

        notificationService.notifySystemAdmins(
                "Inter-Institution Sharing Request #" + request.getId() + " for " + request.getEquipment().getName() + " was REJECTED."
        );

        SharingRequest saved = sharingRequestRepository.save(request);
        equipmentService.recalculateEquipmentStatus(request.getEquipment());

        return saved;
    }

    @Override
    public void deleteRequest(Long id) {
        SharingRequest request = getRequestById(id);

        User currentUser = securityUtil.getCurrentUser();
        boolean isRequester = request.getRequester() != null && request.getRequester().getId().equals(currentUser.getId());
        boolean isOwnerInst = securityUtil.canManageEquipment(currentUser, request.getEquipment());

        if (!securityUtil.isSystemAdmin(currentUser) && !isRequester && !isOwnerInst) {
            throw new AccessDeniedException("Access denied: You are not authorized to delete this sharing request");
        }

        // Clean up linked billing record if exists to prevent foreign key constraint violation
        Optional<Billing> optBilling = billingRepository.findBySharingRequestId(id);
        if (optBilling.isPresent()) {
            billingRepository.delete(optBilling.get());
        }

        sharingRequestRepository.delete(request);
        equipmentService.recalculateEquipmentStatus(request.getEquipment());
    }
}