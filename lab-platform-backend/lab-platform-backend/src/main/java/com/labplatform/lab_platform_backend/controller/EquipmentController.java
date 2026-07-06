package com.labplatform.lab_platform_backend.controller;
import org.springframework.web.bind.annotation.*;
import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.service.EquipmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAllEquipment(Authentication authentication) {

        System.out.println("Authentication object: " + authentication);

        if (authentication != null) {
            System.out.println("Authorities: " + authentication.getAuthorities());
        }

        return equipmentService.getAllEquipment();
    }

    @GetMapping("/shared")
    public List<Equipment> getSharedEquipment() {

        return equipmentService.getSharedEquipment();

    }

    @GetMapping("/{id}")
    public Equipment getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @GetMapping("/search")
    public List<Equipment> searchEquipment(@RequestParam String name) {
        return equipmentService.searchEquipment(name);
    }

    @PostMapping
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentService.createEquipment(equipment);
    }

    @PutMapping("/{id}")
    public Equipment updateEquipment(@PathVariable Long id,
                                     @RequestBody Equipment equipment) {
        return equipmentService.updateEquipment(id, equipment);
    }

    @PutMapping("/{id}/share")
    public Equipment shareEquipment(@PathVariable Long id) {

        return equipmentService.shareEquipment(id);

    }

    @DeleteMapping("/{id}")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
    }
}