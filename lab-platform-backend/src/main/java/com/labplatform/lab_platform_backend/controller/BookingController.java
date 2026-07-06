package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import org.springframework.security.core.Authentication;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.service.BookingService;
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


    @PutMapping("/{id}/approve")
    public Booking approveBooking(@PathVariable Long id) {
        return bookingService.approveBooking(id);
    }

    @PutMapping("/{id}/complete")
    public Booking completeBooking(@PathVariable Long id) {

        return bookingService.completeBooking(id);

    }

    @PutMapping("/{id}/reject")
    public Booking rejectBooking(@PathVariable Long id) {
        return bookingService.rejectBooking(id);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest request,
                                 Authentication authentication) {

        return bookingService.createBooking(
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}