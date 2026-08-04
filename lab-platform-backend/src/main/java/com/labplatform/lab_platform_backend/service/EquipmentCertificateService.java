package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.CertificateAlertDTO;
import com.labplatform.lab_platform_backend.entity.EquipmentCertificate;

import java.util.List;

public interface EquipmentCertificateService {

    List<EquipmentCertificate> getAllCertificates();

    List<EquipmentCertificate> getCertificatesByEquipmentId(Long equipmentId);

    EquipmentCertificate getCertificateById(Long id);

    EquipmentCertificate createCertificate(EquipmentCertificate certificate);

    EquipmentCertificate updateCertificate(Long id, EquipmentCertificate updatedCertificate);

    void deleteCertificate(Long id);

    List<EquipmentCertificate> getExpiringCertificates(int withinDays);

    List<EquipmentCertificate> getExpiredCertificates();

    List<CertificateAlertDTO> getCertificateAlerts();

    void updateCertificateStatuses();

    void checkAndNotifyExpiringCertificates();
}
