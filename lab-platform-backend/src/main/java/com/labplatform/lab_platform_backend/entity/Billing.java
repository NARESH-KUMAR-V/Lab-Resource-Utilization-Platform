package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
@Data
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sharing_request_id")
    private SharingRequest sharingRequest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id")
    private Institution institution; // Requesting / Primary Institution

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owning_institution_id")
    private Institution owningInstitution; // Equipment Owning Institution

    private String department;

    private Double usageDays;

    private Double costPerDay;

    private Double estimatedCost;

    private Double interInstitutionFee = 0.0;

    private Double totalAmount;

    private LocalDate invoiceDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingStatus billingStatus = BillingStatus.ESTIMATED;

    private LocalDateTime createdAt = LocalDateTime.now();
}
