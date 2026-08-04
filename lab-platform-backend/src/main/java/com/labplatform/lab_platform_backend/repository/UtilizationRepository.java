package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Utilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilizationRepository extends JpaRepository<Utilization, Long> {

    Optional<Utilization> findByEquipmentIdAndUserIdAndEndTimeIsNull(Long equipmentId,
                                                                     Long userId);

    List<Utilization> findByEquipmentLaboratoryInstitutionId(Long institutionId);

    @Query("SELECT COALESCE(SUM(u.utilizationHours), 0) FROM Utilization u")
    Double getTotalUtilizationHours();

    // --- Utilization Analytics ---

    List<Utilization> findByEquipmentId(Long equipmentId);

    @Query("SELECT COALESCE(SUM(u.utilizationHours), 0) FROM Utilization u WHERE u.equipment.id = :equipmentId")
    Double getTotalUtilizationHoursByEquipmentId(@Param("equipmentId") Long equipmentId);

    @Query("SELECT u FROM Utilization u WHERE u.equipment.id = :equipmentId AND u.endTime IS NOT NULL ORDER BY u.endTime DESC")
    List<Utilization> findByEquipmentIdOrderByEndTimeDesc(@Param("equipmentId") Long equipmentId);
}