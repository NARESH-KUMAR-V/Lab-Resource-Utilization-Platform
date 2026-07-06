package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String email);
    long countByStatus(String status);
}