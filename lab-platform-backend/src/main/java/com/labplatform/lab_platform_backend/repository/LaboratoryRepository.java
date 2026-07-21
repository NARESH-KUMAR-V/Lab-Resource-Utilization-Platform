package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {

    List<Laboratory> findByInstitutionId(Long institutionId);

}