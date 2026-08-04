package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmail(String email);

    List<Booking> findByEquipmentLaboratoryInstitutionId(Long institutionId);

    List<Booking> findByEquipmentLaboratoryInstitutionIdAndStatusIn(Long institutionId, List<BookingStatus> statuses);

    List<Booking> findByEquipmentLaboratoryInstitutionIdAndStatus(Long institutionId, BookingStatus status);

    List<Booking> findByEquipmentLaboratoryId(Long laboratoryId);

    List<Booking> findByEquipmentLaboratoryIdAndStatusIn(Long laboratoryId, List<BookingStatus> statuses);

    List<Booking> findByEquipmentLaboratoryIdAndStatus(Long laboratoryId, BookingStatus status);

    long countByStatus(BookingStatus status);

    long countByUserEmail(String email);

    long countByUserEmailAndStatus(String email, BookingStatus status);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByStatusIn(List<BookingStatus> statuses);

    List<Booking> findByEquipmentIdAndStatus(Long equipmentId, BookingStatus status);

    List<Booking> findByEquipmentIdAndStatusIn(Long equipmentId, List<BookingStatus> statuses);

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

    // --- Utilization Analytics ---

    List<Booking> findByEquipmentId(Long equipmentId);

    long countByEquipmentId(Long equipmentId);

    @Query("SELECT b FROM Booking b WHERE b.equipment.id = :equipmentId ORDER BY b.endDate DESC")
    List<Booking> findByEquipmentIdOrderByEndDateDesc(@Param("equipmentId") Long equipmentId);

    Optional<Booking> findTopByEquipmentIdAndStatusOrderByEndDateDesc(Long equipmentId, BookingStatus status);
}