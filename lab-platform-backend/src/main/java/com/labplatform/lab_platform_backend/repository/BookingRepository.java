package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String email);

    long countByStatus(BookingStatus status);

    long countByUserEmail(String email);

    long countByUserEmailAndStatus(String email, BookingStatus status);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByStatusIn(List<BookingStatus> statuses);

    List<Booking> findByEquipmentIdAndStatus(Long equipmentId, BookingStatus status);

    List<Booking> findByEquipmentIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long equipmentId,
            BookingStatus status,
            LocalDate endDate,
            LocalDate startDate
    );

    List<Booking> findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long equipmentId,
            List<BookingStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    long countByEquipmentIdAndStatus(Long equipmentId, BookingStatus status);

    List<Booking> findByEquipmentIdAndStatusOrderByWaitingPositionAsc(
            Long equipmentId,
            BookingStatus status
    );
}