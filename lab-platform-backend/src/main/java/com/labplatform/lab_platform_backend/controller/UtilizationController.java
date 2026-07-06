package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Utilization;
import com.labplatform.lab_platform_backend.service.UtilizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilization")
public class UtilizationController {

    private final UtilizationService utilizationService;

    public UtilizationController(UtilizationService utilizationService) {
        this.utilizationService = utilizationService;
    }

    @GetMapping
    public List<Utilization> getAllUtilizations() {
        return utilizationService.getAllUtilizations();
    }

    @GetMapping("/{id}")
    public Utilization getUtilizationById(@PathVariable Long id) {
        return utilizationService.getUtilizationById(id);
    }

    @PostMapping
    public Utilization createUtilization(@RequestBody Utilization utilization) {
        return utilizationService.createUtilization(utilization);
    }

    @PutMapping("/{id}")
    public Utilization updateUtilization(@PathVariable Long id,
                                         @RequestBody Utilization utilization) {
        return utilizationService.updateUtilization(id, utilization);
    }

    @DeleteMapping("/{id}")
    public void deleteUtilization(@PathVariable Long id) {
        utilizationService.deleteUtilization(id);
    }
}