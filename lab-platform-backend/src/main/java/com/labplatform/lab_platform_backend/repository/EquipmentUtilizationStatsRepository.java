package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.EquipmentUtilizationStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentUtilizationStatsRepository extends JpaRepository<EquipmentUtilizationStats, Long> {

    Optional<EquipmentUtilizationStats> findByEquipmentId(Long equipmentId);

    List<EquipmentUtilizationStats> findByEquipmentLaboratoryInstitutionId(Long institutionId);

    List<EquipmentUtilizationStats> findByEquipmentLaboratoryId(Long laboratoryId);

    List<EquipmentUtilizationStats> findAllByOrderByTotalBookingsDesc();

    List<EquipmentUtilizationStats> findAllByOrderByUtilizationPercentageDesc();

    List<EquipmentUtilizationStats> findAllByOrderByTotalBookingsAsc();

    List<EquipmentUtilizationStats> findAllByOrderByIdleDaysDesc();
}
