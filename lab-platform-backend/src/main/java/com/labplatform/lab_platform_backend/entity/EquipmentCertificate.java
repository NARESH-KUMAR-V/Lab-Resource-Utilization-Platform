package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "equipment_certificates")
@Data
public class EquipmentCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false)
    private String certificateName;

    private String certificateNumber;

    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private String issuedBy;

    private String certificateFileUrl;

    @Column(length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateStatus status = CertificateStatus.VALID;

    private Boolean isMandatory = true;
}
