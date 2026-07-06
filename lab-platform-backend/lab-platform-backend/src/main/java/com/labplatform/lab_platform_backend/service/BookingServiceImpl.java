package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.labplatform.lab_platform_backend.service.UtilizationService;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final UtilizationService utilizationService;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
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

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEquipment(equipment);
        booking.setBookingDate(request.getBookingDate());
        booking.setPurpose(request.getPurpose());
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public Booking approveBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("APPROVED");

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.BOOKED);

        equipmentRepository.save(equipment);

        // Create utilization record automatically
        utilizationService.createFromBooking(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking for " +
                        booking.getEquipment().getName() +
                        " has been approved."
        );

        return bookingRepository.save(booking);
    }

    @Override
    public Booking rejectBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("REJECTED");

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking completeBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("COMPLETED");

        Equipment equipment = booking.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepository.save(equipment);

        // Complete utilization record
        utilizationService.completeUtilization(booking);

        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }

    @Override
    public List<Booking> getBookingHistory(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }
}