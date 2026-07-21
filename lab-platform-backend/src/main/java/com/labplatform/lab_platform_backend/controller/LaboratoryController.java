package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Laboratory;
import com.labplatform.lab_platform_backend.service.LaboratoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratories")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    public LaboratoryController(LaboratoryService laboratoryService) {
        this.laboratoryService = laboratoryService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<Laboratory> getAllLaboratories() {
        return laboratoryService.getAllLaboratories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public Laboratory getLaboratoryById(@PathVariable Long id) {
        return laboratoryService.getLaboratoryById(id);
    }

    // Public endpoint used by Register Page
    @GetMapping("/institution/{institutionId}")
    public List<Laboratory> getByInstitution(@PathVariable Long institutionId) {
        return laboratoryService.getLaboratoriesByInstitution(institutionId);
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public Laboratory createLaboratory(@RequestBody Laboratory laboratory) {
        return laboratoryService.createLaboratory(laboratory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('INSTITUTION_ADMIN')")
    public Laboratory updateLaboratory(
            @PathVariable Long id,
            @RequestBody Laboratory laboratory) {

        return laboratoryService.updateLaboratory(id, laboratory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deleteLaboratory(@PathVariable Long id) {
        laboratoryService.deleteLaboratory(id);
    }
}