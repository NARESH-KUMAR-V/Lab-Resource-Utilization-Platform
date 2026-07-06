package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByNameContainingIgnoreCase(String name);
    List<Equipment> findBySharedTrue();
    long countByStatus(EquipmentStatus status);

}