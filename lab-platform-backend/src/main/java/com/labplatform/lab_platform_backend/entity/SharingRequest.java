package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "sharing_requests")
@Data
public class SharingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    private String requestingInstitution;

    private String purpose;

    private LocalDate requestDate;

    private String status;
}
