package com.labplatform.lab_platform_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_utilization_stats")
@Data
public class EquipmentUtilizationStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "equipment_id", nullable = false, unique = true)
    private Equipment equipment;

    private long totalBookings = 0;
    private double totalUsageHours = 0.0;
    private long totalUsageDays = 0;
    private double utilizationPercentage = 0.0;
    private LocalDate lastUsedDate;
    private double avgUsagePerMonth = 0.0;
    private double avgUsagePerWeek = 0.0;
    private long approvedBookings = 0;
    private long completedBookings = 0;
    private long cancelledBookings = 0;
    private long rejectedBookings = 0;
    private long idleDays = 0;
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
