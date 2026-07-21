package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Laboratory;
import com.labplatform.lab_platform_backend.repository.LaboratoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratoryServiceImpl implements LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;

    public LaboratoryServiceImpl(LaboratoryRepository laboratoryRepository) {
        this.laboratoryRepository = laboratoryRepository;
    }

    @Override
    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findAll();
    }

    @Override
    public Laboratory getLaboratoryById(Long id) {
        return laboratoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratory not found"));
    }

    @Override
    public Laboratory createLaboratory(Laboratory laboratory) {
        return laboratoryRepository.save(laboratory);
    }

    @Override
    public Laboratory updateLaboratory(Long id, Laboratory updatedLaboratory) {

        Laboratory laboratory = getLaboratoryById(id);

        laboratory.setName(updatedLaboratory.getName());
        laboratory.setDepartment(updatedLaboratory.getDepartment());
        laboratory.setLocation(updatedLaboratory.getLocation());
        laboratory.setHodName(updatedLaboratory.getHodName());
        laboratory.setInstitution(updatedLaboratory.getInstitution());

        return laboratoryRepository.save(laboratory);
    }

    @Override
    public void deleteLaboratory(Long id) {
        laboratoryRepository.deleteById(id);
    }

    @Override
    public List<Laboratory> getLaboratoriesByInstitution(Long institutionId) {
        return laboratoryRepository.findByInstitutionId(institutionId);
    }
}