package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByNameContainingIgnoreCase(String name);

    List<Equipment> findBySharedTrue();

    long countByStatus(EquipmentStatus status);

    List<Equipment> findByLaboratoryId(Long laboratoryId);

    List<Equipment> findByLaboratoryInstitutionId(Long institutionId);

    @Query("SELECT e FROM Equipment e WHERE e.shared = true AND (e.laboratory.institution.id IS NULL OR e.laboratory.institution.id != :instId)")
    List<Equipment> findExternalSharedEquipment(@Param("instId") Long instId);

}