package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    long countByStatus(String status);
}