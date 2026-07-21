package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Institution;

import java.util.List;

public interface InstitutionService {

    List<Institution> getAllInstitutions();

    Institution getInstitutionById(Long id);

    Institution createInstitution(Institution institution);

    Institution updateInstitution(Long id, Institution institution);

    void deleteInstitution(Long id);
}