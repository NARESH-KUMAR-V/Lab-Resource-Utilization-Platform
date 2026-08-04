package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Laboratory;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.LaboratoryRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaboratoryServiceImpl implements LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;
    private final SecurityUtil securityUtil;

    public LaboratoryServiceImpl(LaboratoryRepository laboratoryRepository, SecurityUtil securityUtil) {
        this.laboratoryRepository = laboratoryRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public List<Laboratory> getAllLaboratories() {
        User user = securityUtil.getCurrentUser();
        if (securityUtil.isSystemAdmin(user)) {
            return laboratoryRepository.findAll();
        }
        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return laboratoryRepository.findAll();
        }
        return laboratoryRepository.findByInstitutionId(instId);
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