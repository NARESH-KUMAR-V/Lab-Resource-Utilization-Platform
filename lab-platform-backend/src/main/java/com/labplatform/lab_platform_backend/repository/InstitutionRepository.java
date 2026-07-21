package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}