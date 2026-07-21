package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Institution;
import com.labplatform.lab_platform_backend.service.InstitutionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    // Public endpoint for registration page
    @GetMapping
    public List<Institution> getAllInstitutions() {
        return institutionService.getAllInstitutions();
    }

    @GetMapping("/{id}")
    public Institution getInstitutionById(@PathVariable Long id) {
        return institutionService.getInstitutionById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public Institution createInstitution(@RequestBody Institution institution) {
        return institutionService.createInstitution(institution);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public Institution updateInstitution(
            @PathVariable Long id,
            @RequestBody Institution institution) {

        return institutionService.updateInstitution(id, institution);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public void deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
    }
}