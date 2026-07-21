package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByNameContainingIgnoreCase(String name);

    List<Equipment> findBySharedTrue();

    long countByStatus(EquipmentStatus status);

    List<Equipment> findByLaboratoryId(Long laboratoryId);

    List<Equipment> findByLaboratoryInstitutionId(Long institutionId);

}