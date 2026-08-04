package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.CertificateStatus;
import com.labplatform.lab_platform_backend.entity.EquipmentCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentCertificateRepository extends JpaRepository<EquipmentCertificate, Long> {

    List<EquipmentCertificate> findByEquipmentId(Long equipmentId);

    List<EquipmentCertificate> findByEquipmentLaboratoryInstitutionId(Long institutionId);

    List<EquipmentCertificate> findByEquipmentLaboratoryInstitutionIdAndStatus(Long institutionId, CertificateStatus status);

    List<EquipmentCertificate> findByEquipmentLaboratoryInstitutionIdAndExpiryDateBetween(Long institutionId, LocalDate start, LocalDate end);

    List<EquipmentCertificate> findByEquipmentLaboratoryInstitutionIdAndExpiryDateBefore(Long institutionId, LocalDate date);

    long countByEquipmentLaboratoryInstitutionIdAndStatus(Long institutionId, CertificateStatus status);

    // --- Laboratory-Scoped Queries for LAB_MANAGER ---

    List<EquipmentCertificate> findByEquipmentLaboratoryId(Long laboratoryId);

    List<EquipmentCertificate> findByEquipmentLaboratoryIdAndStatus(Long laboratoryId, CertificateStatus status);

    List<EquipmentCertificate> findByEquipmentLaboratoryIdAndExpiryDateBetween(Long laboratoryId, LocalDate start, LocalDate end);

    List<EquipmentCertificate> findByEquipmentLaboratoryIdAndExpiryDateBefore(Long laboratoryId, LocalDate date);

    long countByEquipmentLaboratoryIdAndStatus(Long laboratoryId, CertificateStatus status);

    List<EquipmentCertificate> findByStatus(CertificateStatus status);

    List<EquipmentCertificate> findByExpiryDateBefore(LocalDate date);

    List<EquipmentCertificate> findByExpiryDateBetween(LocalDate start, LocalDate end);

    long countByStatus(CertificateStatus status);

    List<EquipmentCertificate> findByExpiryDateBeforeOrderByExpiryDateAsc(LocalDate date);
}
