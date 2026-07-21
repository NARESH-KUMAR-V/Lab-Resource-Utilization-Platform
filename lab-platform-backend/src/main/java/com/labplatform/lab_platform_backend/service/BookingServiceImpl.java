package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final UtilizationService utilizationService;
    private final NotificationService notificationService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            UtilizationService utilizationService,
            NotificationService notificationService) {

        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.utilizationService = utilizationService;
        this.notificationService = notificationService;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @Override
    public Booking createBooking(BookingRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (equipment.getStatus() != EquipmentStatus.AVAILABLE &&
                equipment.getStatus() != EquipmentStatus.BOOKED) {

            throw new RuntimeException(
                    "Equipment is currently "
                            + equipment.getStatus()
                            + " and cannot be booked."
            );
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date.");
        }

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                equipment.getId(),
                                List.of(
                                        BookingStatus.APPROVED,
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

            notificationService.createNotification(
                    manager,
                    booking.getStatus() == BookingStatus.WAITING
                            ? "A new booking has been added to the waiting list for "
                              + equipment.getName() + "."
                            : "New booking request from "
                              + user.getName()
                              + " for "
                              + equipment.getName() + "."
            );

        }

        return savedBooking;
    }

    @Override
    public Booking approveBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus(BookingStatus.APPROVED);
        booking.setWaitingPosition(0);

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.BOOKED);

        equipmentRepository.save(equipment);

        utilizationService.createFromBooking(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for "
                        + equipment.getName()
                        + " has been approved."
        );

        Booking approvedBooking = bookingRepository.save(booking);

        List<Booking> waitingBookings =
                bookingRepository.findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
                        equipment.getId(),
                        BookingStatus.WAITING
                );

        for (int i = 0; i < waitingBookings.size(); i++) {
            waitingBookings.get(i).setWaitingPosition(i + 1);
        }

        bookingRepository.saveAll(waitingBookings);

        return approvedBooking;
    }

    @Override
    public Booking rejectBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus(BookingStatus.REJECTED);

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for "
                        + equipment.getName()
                        + " has been rejected."
        );

        return bookingRepository.save(booking);
    }

    @Override
    public Booking completeBooking(Long id) {

        Booking booking = getBookingById(id);

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
                            + equipment.getName()
                            + " is now pending approval. Please wait for the Lab Manager to approve it."
            );

            for (int i = 1; i < waitingBookings.size(); i++) {
                Booking waiting = waitingBookings.get(i);
                waiting.setWaitingPosition(i);
                bookingRepository.save(waiting);
            }
        }

        return completedBooking;
    }

    @Override
    public void deleteBooking(Long id) {

        Booking booking = getBookingById(id);

        bookingRepository.delete(booking);
    }

    @Override
    public List<Booking> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Booking> getBookingHistory(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Booking> getPendingBookings() {

        return bookingRepository.findByStatusIn(
                List.of(
                        BookingStatus.PENDING,
                        BookingStatus.WAITING
                )
        );
    }
}