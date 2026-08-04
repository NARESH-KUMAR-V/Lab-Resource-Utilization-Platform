package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.SharingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SharingRequestRepository extends JpaRepository<SharingRequest, Long> {

    List<SharingRequest> findByRequesterEmail(String email);

    @Query("SELECT s FROM SharingRequest s LEFT JOIN s.equipment e LEFT JOIN e.laboratory el LEFT JOIN el.institution ei LEFT JOIN s.requester r LEFT JOIN r.institution ri LEFT JOIN r.laboratory rl LEFT JOIN rl.institution rli WHERE ei.id = :instId OR ri.id = :instId OR rli.id = :instId")
    List<SharingRequest> findByInstitutionId(@Param("instId") Long instId);

    @Query("SELECT s FROM SharingRequest s LEFT JOIN s.equipment e LEFT JOIN e.laboratory l LEFT JOIN l.institution i WHERE i.id IS NOT NULL AND i.id = :instId")
    List<SharingRequest> findIncomingRequestsByInstitutionId(@Param("instId") Long instId);

    @Query("SELECT s FROM SharingRequest s LEFT JOIN s.requester r LEFT JOIN r.institution i LEFT JOIN r.laboratory l LEFT JOIN l.institution li WHERE r.id = :userId OR (i.id IS NOT NULL AND i.id = :instId) OR (li.id IS NOT NULL AND li.id = :instId)")
    List<SharingRequest> findOutgoingRequestsByUserOrInstitution(@Param("userId") Long userId, @Param("instId") Long instId);

    long countByStatus(String status);

    // Overlapping APPROVED or ACTIVE sharing requests for equipment
    @Query("SELECT s FROM SharingRequest s WHERE s.equipment.id = :equipmentId AND s.status IN ('APPROVED', 'ACTIVE') AND s.startDate <= :endDate AND s.endDate >= :startDate")
    List<SharingRequest> findOverlappingSharingRequests(
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Check if an active sharing request exists for equipment on a specific date
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SharingRequest s WHERE s.equipment.id = :equipmentId AND (s.status = 'ACTIVE' OR (s.status = 'APPROVED' AND s.startDate <= :date AND s.endDate >= :date))")
    boolean existsActiveSharingForDate(@Param("equipmentId") Long equipmentId, @Param("date") LocalDate date);

    // Requests to activate (status APPROVED and startDate <= today and endDate >= today)
    @Query("SELECT s FROM SharingRequest s WHERE s.status = 'APPROVED' AND s.startDate <= :today AND s.endDate >= :today")
    List<SharingRequest> findRequestsToActivate(@Param("today") LocalDate today);

    // Requests to complete (status IN ('APPROVED', 'ACTIVE') and endDate < today)
    @Query("SELECT s FROM SharingRequest s WHERE s.status IN ('APPROVED', 'ACTIVE') AND s.endDate < :today")
    List<SharingRequest> findRequestsToComplete(@Param("today") LocalDate today);
}