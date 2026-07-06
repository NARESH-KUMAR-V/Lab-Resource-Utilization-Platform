package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BookingRequest;
import com.labplatform.lab_platform_backend.entity.Booking;

import java.util.List;

public interface BookingService {

    List<Booking> getAllBookings();

    Booking getBookingById(Long id);

    Booking createBooking(BookingRequest request, String userEmail);

    Booking approveBooking(Long id);

    Booking rejectBooking(Long id);

    Booking completeBooking(Long id);

    void deleteBooking(Long id);

    List<Booking> getMyBookings(String userEmail);

    // NEW
    List<Booking> getBookingHistory(String userEmail);
}