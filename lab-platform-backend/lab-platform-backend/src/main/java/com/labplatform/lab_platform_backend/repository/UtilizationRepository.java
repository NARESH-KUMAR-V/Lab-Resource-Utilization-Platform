package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Utilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface UtilizationRepository extends JpaRepository<Utilization, Long> {

    Optional<Utilization> findByEquipmentIdAndUserIdAndEndTimeIsNull(Long equipmentId,
                                                                     Long userId);

    @Query("SELECT COALESCE(SUM(u.utilizationHours), 0) FROM Utilization u")
    Double getTotalUtilizationHours();

}