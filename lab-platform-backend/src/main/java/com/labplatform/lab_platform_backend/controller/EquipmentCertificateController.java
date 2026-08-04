package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.CertificateAlertDTO;
import com.labplatform.lab_platform_backend.entity.EquipmentCertificate;
import com.labplatform.lab_platform_backend.service.EquipmentCertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/certificates")
public class EquipmentCertificateController {

    private final EquipmentCertificateService certificateService;

    public EquipmentCertificateController(EquipmentCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentCertificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<CertificateAlertDTO> getCertificateAlerts() {
        return certificateService.getCertificateAlerts();
    }

    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentCertificate> getCertificatesByEquipment(@PathVariable Long equipmentId) {
        return certificateService.getCertificatesByEquipmentId(equipmentId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public EquipmentCertificate getCertificateById(@PathVariable Long id) {
        return certificateService.getCertificateById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public EquipmentCertificate createCertificate(@RequestBody EquipmentCertificate certificate) {
        return certificateService.createCertificate(certificate);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public EquipmentCertificate updateCertificate(
            @PathVariable Long id,
            @RequestBody EquipmentCertificate certificate) {
        return certificateService.updateCertificate(id, certificate);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public void deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentCertificate> getExpiringCertificates(
            @RequestParam(defaultValue = "30") int withinDays) {
        return certificateService.getExpiringCertificates(withinDays);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<EquipmentCertificate> getExpiredCertificates() {
        return certificateService.getExpiredCertificates();
    }

    @PostMapping("/check-notifications")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> checkNotifications() {
        certificateService.checkAndNotifyExpiringCertificates();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> uploadCertificateFile(
            @RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = "uploads/certificates";

        Files.createDirectories(Paths.get(uploadDir));

        String fileName = UUID.randomUUID() + "_"
                + StringUtils.cleanPath(file.getOriginalFilename());

        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok("/uploads/certificates/" + fileName);
    }
}
