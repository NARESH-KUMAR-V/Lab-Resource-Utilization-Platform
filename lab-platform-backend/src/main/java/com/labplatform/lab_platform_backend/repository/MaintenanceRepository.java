package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Maintenance;
import com.labplatform.lab_platform_backend.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    long countByStatus(MaintenanceStatus status);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    List<Maintenance> findByTechnicianEmail(String email);

    List<Maintenance> findByEquipmentId(Long equipmentId);

}