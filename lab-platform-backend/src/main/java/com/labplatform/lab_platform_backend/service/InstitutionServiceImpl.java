package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Institution;
import com.labplatform.lab_platform_backend.repository.InstitutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionServiceImpl(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }

    @Override
    public Institution getInstitutionById(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Institution not found"));
    }

    @Override
    public Institution createInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    @Override
    public Institution updateInstitution(Long id, Institution updatedInstitution) {

        Institution institution = getInstitutionById(id);

        institution.setName(updatedInstitution.getName());
        institution.setAddress(updatedInstitution.getAddress());
        institution.setEmail(updatedInstitution.getEmail());
        institution.setPhone(updatedInstitution.getPhone());

        return institutionRepository.save(institution);
    }

    @Override
    public void deleteInstitution(Long id) {
        institutionRepository.deleteById(id);
    }
}