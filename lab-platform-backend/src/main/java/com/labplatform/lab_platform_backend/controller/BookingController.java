package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/my")
    public List<Booking> getMyBookings(Authentication authentication) {
        return bookingService.getMyBookings(authentication.getName());
    }

    @GetMapping("/history")
    public List<Booking> getBookingHistory(Authentication authentication) {
        return bookingService.getBookingHistory(authentication.getName());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<Booking> getPendingBookings() {
        return bookingService.getPendingBookings();
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Booking createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        return bookingService.createBooking(
                request,
                authentication.getName()
        );
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Booking approveBooking(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Booking rejectBooking(@PathVariable Long id) {
        return bookingService.rejectBooking(id);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Booking completeBooking(@PathVariable Long id) {
        return bookingService.completeBooking(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}