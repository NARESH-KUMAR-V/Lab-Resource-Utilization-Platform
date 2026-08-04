package com.labplatform.lab_platform_backend.dto;

import com.labplatform.lab_platform_backend.entity.CertificateStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateAlertDTO {
    private Long id;
    private Long equipmentId;
    private String equipmentName;
    private String certificateName;
    private String certificateNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issuedBy;
    private String remarks;
    private long daysRemaining;
    private String remainingText;
    private CertificateStatus status;
    private Boolean isMandatory;
    private Boolean hasOpenMaintenance;
}
