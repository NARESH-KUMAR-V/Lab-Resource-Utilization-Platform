package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Laboratory;

import java.util.List;

public interface LaboratoryService {

    List<Laboratory> getAllLaboratories();

    Laboratory getLaboratoryById(Long id);

    Laboratory createLaboratory(Laboratory laboratory);

    Laboratory updateLaboratory(Long id, Laboratory laboratory);

    void deleteLaboratory(Long id);

    List<Laboratory> getLaboratoriesByInstitution(Long institutionId);
}