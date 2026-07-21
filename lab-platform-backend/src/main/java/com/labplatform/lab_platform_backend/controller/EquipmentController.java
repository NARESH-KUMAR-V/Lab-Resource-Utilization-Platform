package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAllEquipment(Authentication authentication) {

        return equipmentService.getAllEquipment(authentication.getName());

    }

    @GetMapping("/{id}")
    public Equipment getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @GetMapping("/search")
    public List<Equipment> searchEquipment(@RequestParam String name) {
        return equipmentService.searchEquipment(name);
    }

    @GetMapping("/shared")
    public List<Equipment> getSharedEquipment() {
        return equipmentService.getSharedEquipment();
    }

    @GetMapping("/laboratory/{laboratoryId}")
    public List<Equipment> getEquipmentByLaboratory(@PathVariable Long laboratoryId) {
        return equipmentService.getEquipmentByLaboratory(laboratoryId);
    }

    @PostMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentService.createEquipment(equipment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Equipment updateEquipment(
            @PathVariable Long id,
            @RequestBody Equipment equipment) {

        return equipmentService.updateEquipment(id, equipment);
    }

    @PutMapping("/{id}/share")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Equipment shareEquipment(@PathVariable Long id) {
        return equipmentService.shareEquipment(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = "uploads/equipment";

        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_"
                + StringUtils.cleanPath(file.getOriginalFilename());

        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/equipment/" + fileName);
    }
}