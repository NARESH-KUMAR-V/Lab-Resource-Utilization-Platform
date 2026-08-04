package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final UtilizationService utilizationService;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;
    private final SharingRequestRepository sharingRequestRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            UtilizationService utilizationService,
            NotificationService notificationService,
            SecurityUtil securityUtil,
            SharingRequestRepository sharingRequestRepository) {

        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.utilizationService = utilizationService;
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
        this.sharingRequestRepository = sharingRequestRepository;
    }

    private void cleanOverlappingWaitingBookings() {
        List<Booking> approvedBookings = bookingRepository.findByStatus(BookingStatus.APPROVED);

        for (Booking approved : approvedBookings) {
            List<Booking> overlapping =
                    bookingRepository
                            .findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                    approved.getEquipment().getId(),
                                    List.of(BookingStatus.WAITING),
                                    approved.getEndDate(),
                                    approved.getStartDate()
                            );

            if (!overlapping.isEmpty()) {
                for (Booking b : overlapping) {
                    b.setStatus(BookingStatus.REJECTED);
                    b.setWaitingPosition(0);
                    notificationService.createNotification(
                            b.getUser(),
                            "Your waiting booking request for "
                                    + b.getEquipment().getName()
                                    + " was automatically rejected due to an overlapping approved booking."
                    );
                }
                bookingRepository.saveAll(overlapping);
            }
        }
    }

    @Override
    public List<Booking> getAllBookings() {
        cleanOverlappingWaitingBookings();
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return bookingRepository.findAll();
        }
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            return bookingRepository.findByEquipmentLaboratoryId(user.getLaboratory().getId());
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return List.of();
        }
        return bookingRepository.findByEquipmentLaboratoryInstitutionId(instId);
    }

    @Override
    public Booking getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewBooking(user, booking)) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this booking");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingsByEquipmentId(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You are not authorized to view bookings for this equipment");
        }

        return bookingRepository.findByEquipmentIdAndStatusIn(
                equipmentId,
                List.of(BookingStatus.APPROVED, BookingStatus.PENDING, BookingStatus.WAITING)
        );
    }

    @Override
    public Booking createBooking(BookingRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!securityUtil.canViewEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You are not authorized to book this equipment");
        }

        if (equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE ||
            equipment.getStatus() == EquipmentStatus.OUT_OF_SERVICE ||
            equipment.getStatus() == EquipmentStatus.RETIRED) {

            throw new RuntimeException(
                    "Equipment is currently "
                            + equipment.getStatus()
                            + " and cannot be booked."
            );
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new RuntimeException("Start date and end date are required.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date.");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book equipment for past dates.");
        }

        // Check for inter-institution sharing conflict
        List<SharingRequest> overlappingSharing = sharingRequestRepository.findOverlappingSharingRequests(
                equipment.getId(), request.getStartDate(), request.getEndDate()
        );

        if (!overlappingSharing.isEmpty()) {
            SharingRequest sharing = overlappingSharing.get(0);
            throw new RuntimeException(
                    "This equipment is allocated to another institution (" + sharing.getRequestingInstitution()
                    + ") for inter-institution sharing from " + sharing.getStartDate() + " to " + sharing.getEndDate()
                    + ". Please select different dates."
            );
        }

        // Check if there is any active booking (APPROVED, PENDING, WAITING) with overlapping date range
        List<Booking> overlappingBookings =
                bookingRepository
                        .findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                equipment.getId(),
                                List.of(
                                        BookingStatus.APPROVED,
                                        BookingStatus.PENDING,
                                        BookingStatus.WAITING
                                ),
                                request.getEndDate(),
                                request.getStartDate()
                        );

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setEquipment(equipment);
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setPurpose(request.getPurpose());

        long days =
                request.getStartDate()
                        .until(request.getEndDate())
                        .getDays() + 1;

        booking.setUtilizationCost(
                Math.round(days * equipment.getCostPerDay())
        );

        if (!overlappingBookings.isEmpty()) {

            long waitingCount =
                    bookingRepository.countByEquipmentIdAndStatus(
                            equipment.getId(),
                            BookingStatus.WAITING
                    );

            booking.setStatus(BookingStatus.WAITING);
            booking.setWaitingPosition((int) waitingCount + 1);

        } else {

            booking.setStatus(BookingStatus.PENDING);
            booking.setWaitingPosition(0);

        }

        Booking savedBooking = bookingRepository.save(booking);

        List<User> managers = userRepository.findByRole(Role.LAB_MANAGER);

        for (User manager : managers) {
            if (securityUtil.isSameInstitution(manager, equipment)) {
                notificationService.createNotification(
                        manager,
                        booking.getStatus() == BookingStatus.WAITING
                                ? "A new booking has been added to the waiting list for "
                                  + equipment.getName() + " due to overlapping requested dates."
                                : "New booking request from "
                                  + user.getName()
                                  + " for "
                                  + equipment.getName() + "."
                );
            }
        }

        return savedBooking;
    }

    @Override
    public Booking approveBooking(Long id) {

        Booking booking = getBookingById(id);

        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.canManageBooking(currentUser, booking)) {
            throw new AccessDeniedException("Access denied: You cannot approve bookings for equipment belonging to another laboratory or institution");
        }

        // Prevent approving a booking that conflicts with another approved booking
        List<Booking> approvedBookings =
                bookingRepository
                        .findByEquipmentIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                booking.getEquipment().getId(),
                                BookingStatus.APPROVED,
                                booking.getEndDate(),
                                booking.getStartDate()
                        );

        approvedBookings.removeIf(b -> b.getId().equals(booking.getId()));

        if (!approvedBookings.isEmpty()) {
            throw new RuntimeException(
                    "Cannot approve booking. Equipment is already booked for the selected dates."
            );
        }

        // Also check if approved sharing conflicts with this booking
        List<SharingRequest> overlappingSharing = sharingRequestRepository.findOverlappingSharingRequests(
                booking.getEquipment().getId(), booking.getStartDate(), booking.getEndDate()
        );

        if (!overlappingSharing.isEmpty()) {
            SharingRequest sharing = overlappingSharing.get(0);
            throw new RuntimeException(
                    "Cannot approve booking. Equipment is allocated to another institution for inter-institution sharing from "
                    + sharing.getStartDate() + " to " + sharing.getEndDate() + "."
            );
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setWaitingPosition(0);

        Equipment equipment = booking.getEquipment();
        if (booking.getStartDate().isEqual(LocalDate.now())) {
            equipment.setStatus(EquipmentStatus.BOOKED);
            equipmentRepository.save(equipment);
        }

        utilizationService.createFromBooking(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for "
                        + equipment.getName()
                        + " has been approved."
        );

        Booking approvedBooking = bookingRepository.save(booking);

        // Auto-reject any WAITING or PENDING bookings for this equipment that overlap with the newly approved booking
        List<Booking> overlappingWaitingOrPending =
                bookingRepository
                        .findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                equipment.getId(),
                                List.of(BookingStatus.WAITING, BookingStatus.PENDING),
                                booking.getEndDate(),
                                booking.getStartDate()
                        );

        overlappingWaitingOrPending.removeIf(b -> b.getId().equals(booking.getId()));

        for (Booking overlapping : overlappingWaitingOrPending) {
            overlapping.setStatus(BookingStatus.REJECTED);
            overlapping.setWaitingPosition(0);
            notificationService.createNotification(
                    overlapping.getUser(),
                    "Your booking request for "
                            + equipment.getName()
                            + " (" + overlapping.getStartDate() + " to " + overlapping.getEndDate() + ")"
                            + " has been automatically rejected due to an overlapping approved booking."
            );
        }

        if (!overlappingWaitingOrPending.isEmpty()) {
            bookingRepository.saveAll(overlappingWaitingOrPending);
        }

        List<Booking> remainingWaitingBookings =
                bookingRepository.findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
                        equipment.getId(),
                        BookingStatus.WAITING
                );

        for (int i = 0; i < remainingWaitingBookings.size(); i++) {
            remainingWaitingBookings.get(i).setWaitingPosition(i + 1);
        }

        bookingRepository.saveAll(remainingWaitingBookings);

        return approvedBooking;
    }

    @Override
    public Booking rejectBooking(Long id) {

        Booking booking = getBookingById(id);

        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.canManageBooking(currentUser, booking)) {
            throw new AccessDeniedException("Access denied: You cannot reject bookings for equipment belonging to another laboratory or institution");
        }

        booking.setStatus(BookingStatus.REJECTED);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for "
                        + booking.getEquipment().getName()
                        + " has been rejected."
        );

        Booking rejectedBooking = bookingRepository.save(booking);

        List<Booking> waitingBookings =
                bookingRepository.findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
                        booking.getEquipment().getId(),
                        BookingStatus.WAITING
                );

        if (!waitingBookings.isEmpty()) {

            Booking nextBooking = waitingBookings.get(0);

            nextBooking.setStatus(BookingStatus.PENDING);
            nextBooking.setWaitingPosition(0);

            bookingRepository.save(nextBooking);

            notificationService.createNotification(
                    nextBooking.getUser(),
                    "Your booking for "
                            + booking.getEquipment().getName()
                            + " is now pending approval."
            );

            for (int i = 1; i < waitingBookings.size(); i++) {
                Booking waiting = waitingBookings.get(i);
                waiting.setWaitingPosition(i);
            }

            bookingRepository.saveAll(
                    waitingBookings.subList(1, waitingBookings.size())
            );
        }

        return rejectedBooking;
    }

    @Override
    public Booking completeBooking(Long id) {

        Booking booking = getBookingById(id);

        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.canManageBooking(currentUser, booking)) {
            throw new AccessDeniedException("Access denied: You cannot complete bookings for equipment belonging to another laboratory or institution");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        utilizationService.completeUtilization(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for "
                        + equipment.getName()
                        + " has been completed."
        );

        Booking completedBooking = bookingRepository.save(booking);

        List<Booking> waitingBookings =
                bookingRepository.findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
                        equipment.getId(),
                        BookingStatus.WAITING
                );

        if (!waitingBookings.isEmpty()) {

            Booking nextBooking = waitingBookings.get(0);

            nextBooking.setStatus(BookingStatus.PENDING);
            nextBooking.setWaitingPosition(0);

            bookingRepository.save(nextBooking);

            notificationService.createNotification(
                    nextBooking.getUser(),
                    "Your booking for "
                            + booking.getEquipment().getName()
                            + " is now pending approval."
            );

            for (int i = 1; i < waitingBookings.size(); i++) {
                Booking waiting = waitingBookings.get(i);
                waiting.setWaitingPosition(i);
            }

            bookingRepository.saveAll(waitingBookings.subList(1, waitingBookings.size()));
        }

        return completedBooking;
    }

    @Override
    public void deleteBooking(Long id) {

        Booking booking = getBookingById(id);

        User currentUser = securityUtil.getCurrentUser();
        if (!securityUtil.canManageBooking(currentUser, booking)) {
            throw new AccessDeniedException("Access denied: You cannot delete bookings for equipment belonging to another laboratory or institution");
        }

        bookingRepository.delete(booking);

        List<Booking> waitingBookings =
                bookingRepository.findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
                        booking.getEquipment().getId(),
                        BookingStatus.WAITING
                );

        for (int i = 0; i < waitingBookings.size(); i++) {
            waitingBookings.get(i).setWaitingPosition(i + 1);
        }

        bookingRepository.saveAll(waitingBookings);
    }

    @Override
    public List<Booking> getMyBookings(String userEmail) {
        cleanOverlappingWaitingBookings();
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Booking> getBookingHistory(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Booking> getPendingBookings() {
        cleanOverlappingWaitingBookings();
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return bookingRepository.findByStatusIn(
                    List.of(BookingStatus.PENDING, BookingStatus.WAITING)
            );
        }
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            return bookingRepository.findByEquipmentLaboratoryIdAndStatusIn(
                    user.getLaboratory().getId(), List.of(BookingStatus.PENDING, BookingStatus.WAITING)
            );
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) return List.of();
        return bookingRepository.findByEquipmentLaboratoryInstitutionIdAndStatusIn(
                instId, List.of(BookingStatus.PENDING, BookingStatus.WAITING)
        );
    }

    @Override
    public List<Booking> getWaitingBookings() {
        cleanOverlappingWaitingBookings();
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return bookingRepository.findByStatus(BookingStatus.WAITING);
        }
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            return bookingRepository.findByEquipmentLaboratoryIdAndStatus(
                    user.getLaboratory().getId(), BookingStatus.WAITING
            );
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) return List.of();
        return bookingRepository.findByEquipmentLaboratoryInstitutionIdAndStatus(
                instId, BookingStatus.WAITING
        );
    }
}