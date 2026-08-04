package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.CertificateAlertDTO;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.EquipmentCertificateRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.MaintenanceRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentCertificateServiceImpl implements EquipmentCertificateService {

    private final EquipmentCertificateRepository certificateRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public EquipmentCertificateServiceImpl(
            EquipmentCertificateRepository certificateRepository,
            EquipmentRepository equipmentRepository,
            MaintenanceRepository maintenanceRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            SecurityUtil securityUtil) {

        this.certificateRepository = certificateRepository;
        this.equipmentRepository = equipmentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentCertificate> getAllCertificates() {
        User user = securityUtil.getCurrentUser();
        List<EquipmentCertificate> list;

        if (securityUtil.isSystemAdmin(user)) {
            list = certificateRepository.findAll();
        } else if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            list = certificateRepository.findByEquipmentLaboratoryId(user.getLaboratory().getId());
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId == null) return List.of();
            list = certificateRepository.findByEquipmentLaboratoryInstitutionId(instId);
        }

        // Dynamically set computing status on returned list
        list.forEach(cert -> cert.setStatus(computeStatus(cert.getExpiryDate())));
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentCertificate> getCertificatesByEquipmentId(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You are not authorized to view certificates for this equipment");
        }
        List<EquipmentCertificate> list = certificateRepository.findByEquipmentId(equipmentId);
        list.forEach(cert -> cert.setStatus(computeStatus(cert.getExpiryDate())));
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentCertificate getCertificateById(Long id) {
        EquipmentCertificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canViewCertificate(user, cert)) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this certificate");
        }
        cert.setStatus(computeStatus(cert.getExpiryDate()));
        return cert;
    }

    @Override
    public EquipmentCertificate createCertificate(EquipmentCertificate certificate) {
        Equipment equipment = equipmentRepository.findById(certificate.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: You cannot create certificates for equipment belonging to another laboratory or institution");
        }

        certificate.setEquipment(equipment);
        if (certificate.getIsMandatory() == null) {
            certificate.setIsMandatory(true);
        }
        certificate.setStatus(computeStatus(certificate.getExpiryDate()));

        EquipmentCertificate saved = certificateRepository.save(certificate);
        checkAndNotifyExpiringCertificates();
        return saved;
    }

    @Override
    public EquipmentCertificate updateCertificate(Long id, EquipmentCertificate updatedCertificate) {
        EquipmentCertificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageCertificate(user, certificate)) {
            throw new AccessDeniedException("Access denied: You cannot modify certificates belonging to another laboratory or institution");
        }

        Equipment equipment = equipmentRepository.findById(updatedCertificate.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!securityUtil.canManageEquipment(user, equipment)) {
            throw new AccessDeniedException("Access denied: Target equipment belongs to another laboratory or institution");
        }

        certificate.setEquipment(equipment);
        certificate.setCertificateName(updatedCertificate.getCertificateName());
        certificate.setCertificateNumber(updatedCertificate.getCertificateNumber());
        certificate.setIssueDate(updatedCertificate.getIssueDate());
        certificate.setExpiryDate(updatedCertificate.getExpiryDate());
        certificate.setIssuedBy(updatedCertificate.getIssuedBy());
        certificate.setRemarks(updatedCertificate.getRemarks());

        if (updatedCertificate.getIsMandatory() != null) {
            certificate.setIsMandatory(updatedCertificate.getIsMandatory());
        }

        if (updatedCertificate.getCertificateFileUrl() != null) {
            certificate.setCertificateFileUrl(updatedCertificate.getCertificateFileUrl());
        }

        certificate.setStatus(computeStatus(certificate.getExpiryDate()));

        EquipmentCertificate saved = certificateRepository.save(certificate);
        checkAndNotifyExpiringCertificates();
        return saved;
    }

    @Override
    public void deleteCertificate(Long id) {
        EquipmentCertificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));

        User user = securityUtil.getCurrentUser();
        if (!securityUtil.canManageCertificate(user, certificate)) {
            throw new AccessDeniedException("Access denied: You cannot delete certificates belonging to another laboratory or institution");
        }

        certificateRepository.delete(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentCertificate> getExpiringCertificates(int withinDays) {
        User user = securityUtil.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(withinDays);
        List<EquipmentCertificate> list;

        if (securityUtil.isSystemAdmin(user)) {
            list = certificateRepository.findByExpiryDateBetween(today, futureDate);
        } else if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            list = certificateRepository.findByEquipmentLaboratoryIdAndExpiryDateBetween(user.getLaboratory().getId(), today, futureDate);
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId == null) return List.of();
            list = certificateRepository.findByEquipmentLaboratoryInstitutionIdAndExpiryDateBetween(instId, today, futureDate);
        }

        list.forEach(cert -> cert.setStatus(computeStatus(cert.getExpiryDate())));
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentCertificate> getExpiredCertificates() {
        User user = securityUtil.getCurrentUser();
        LocalDate today = LocalDate.now();
        List<EquipmentCertificate> list;

        if (securityUtil.isSystemAdmin(user)) {
            list = certificateRepository.findByExpiryDateBefore(today);
        } else if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            list = certificateRepository.findByEquipmentLaboratoryIdAndExpiryDateBefore(user.getLaboratory().getId(), today);
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId == null) return List.of();
            list = certificateRepository.findByEquipmentLaboratoryInstitutionIdAndExpiryDateBefore(instId, today);
        }

        list.forEach(cert -> cert.setStatus(CertificateStatus.EXPIRED));
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateAlertDTO> getCertificateAlerts() {
        User user = securityUtil.getCurrentUser();
        List<EquipmentCertificate> certs;

        if (securityUtil.isSystemAdmin(user)) {
            certs = certificateRepository.findAll();
        } else if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            certs = certificateRepository.findByEquipmentLaboratoryId(user.getLaboratory().getId());
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId == null) return List.of();
            certs = certificateRepository.findByEquipmentLaboratoryInstitutionId(instId);
        }

        LocalDate today = LocalDate.now();
        List<CertificateAlertDTO> alerts = new ArrayList<>();

        for (EquipmentCertificate cert : certs) {
            if (cert.getExpiryDate() == null) continue;
            long daysRemaining = ChronoUnit.DAYS.between(today, cert.getExpiryDate());

            if (daysRemaining <= 30) {
                CertificateAlertDTO dto = new CertificateAlertDTO();
                dto.setId(cert.getId());
                dto.setEquipmentId(cert.getEquipment().getId());
                dto.setEquipmentName(cert.getEquipment().getName());
                dto.setCertificateName(cert.getCertificateName());
                dto.setCertificateNumber(cert.getCertificateNumber());
                dto.setIssueDate(cert.getIssueDate());
                dto.setExpiryDate(cert.getExpiryDate());
                dto.setIssuedBy(cert.getIssuedBy());
                dto.setRemarks(cert.getRemarks());
                dto.setDaysRemaining(daysRemaining);
                dto.setStatus(computeStatus(cert.getExpiryDate()));
                dto.setIsMandatory(cert.getIsMandatory() != null ? cert.getIsMandatory() : true);

                if (daysRemaining < 0) {
                    dto.setRemainingText("Expired " + Math.abs(daysRemaining) + " day(s) ago (" + cert.getExpiryDate() + ")");
                } else if (daysRemaining == 0) {
                    dto.setRemainingText("Expires TODAY (" + cert.getExpiryDate() + ")");
                } else {
                    dto.setRemainingText(daysRemaining + " day(s) remaining (Expires " + cert.getExpiryDate() + ")");
                }

                boolean hasOpen = maintenanceRepository.existsByEquipmentIdAndStatusIn(
                        cert.getEquipment().getId(),
                        List.of(MaintenanceStatus.PENDING, MaintenanceStatus.IN_PROGRESS)
                );
                dto.setHasOpenMaintenance(hasOpen);

                alerts.add(dto);
            }
        }

        return alerts.stream()
                .sorted(Comparator.comparing(CertificateAlertDTO::getExpiryDate))
                .collect(Collectors.toList());
    }

    @Override
    public void updateCertificateStatuses() {
        List<EquipmentCertificate> allCertificates = certificateRepository.findAll();

        for (EquipmentCertificate cert : allCertificates) {
            if (cert.getIsMandatory() == null) {
                cert.setIsMandatory(true);
            }
            CertificateStatus newStatus = computeStatus(cert.getExpiryDate());
            if (!newStatus.equals(cert.getStatus())) {
                cert.setStatus(newStatus);
                certificateRepository.save(cert);
            }
        }
    }

    @Override
    public void checkAndNotifyExpiringCertificates() {
        LocalDate today = LocalDate.now();
        List<EquipmentCertificate> allCerts = certificateRepository.findAll();

        for (EquipmentCertificate cert : allCerts) {
            if (cert.getEquipment() == null || cert.getExpiryDate() == null) continue;
            long daysRemaining = ChronoUnit.DAYS.between(today, cert.getExpiryDate());

            String msg = null;
            if (daysRemaining < 0) {
                msg = "Calibration certificate '" + cert.getCertificateName()
                        + "' for equipment '" + cert.getEquipment().getName()
                        + "' has EXPIRED on " + cert.getExpiryDate() + ". Renewal is required.";
            } else if (daysRemaining <= 7) {
                msg = "Calibration certificate '" + cert.getCertificateName()
                        + "' for equipment '" + cert.getEquipment().getName()
                        + "' will expire in 7 days (" + cert.getExpiryDate() + ").";
            } else if (daysRemaining <= 30) {
                msg = "Calibration certificate '" + cert.getCertificateName()
                        + "' for equipment '" + cert.getEquipment().getName()
                        + "' will expire in 30 days (" + cert.getExpiryDate() + ").";
            }

            if (msg != null) {
                notifyAuthorizedUsers(cert.getEquipment(), msg);
            }
        }
    }

    private void notifyAuthorizedUsers(Equipment equipment, String message) {
        List<User> managers = userRepository.findByRole(Role.LAB_MANAGER);
        List<User> admins = userRepository.findByRole(Role.INSTITUTION_ADMIN);
        List<User> sysAdmins = userRepository.findByRole(Role.SYSTEM_ADMIN);

        for (User u : managers) {
            if (securityUtil.isSameInstitution(u, equipment)) {
                notificationService.createNotification(u, message);
            }
        }
        for (User u : admins) {
            if (securityUtil.isSameInstitution(u, equipment)) {
                notificationService.createNotification(u, message);
            }
        }
        for (User u : sysAdmins) {
            notificationService.createNotification(u, message);
        }
    }

    private CertificateStatus computeStatus(LocalDate expiryDate) {
        if (expiryDate == null) return CertificateStatus.VALID;
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return CertificateStatus.EXPIRED;
        } else if (!expiryDate.isAfter(today.plusDays(30))) {
            return CertificateStatus.EXPIRING_SOON;
        } else {
            return CertificateStatus.VALID;
        }
    }
}
