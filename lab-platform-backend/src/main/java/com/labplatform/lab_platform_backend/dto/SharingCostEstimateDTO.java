package com.labplatform.lab_platform_backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SharingCostEstimateDTO {
    private Long equipmentId;
    private String equipmentName;
    private Double costPerDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long durationDays;
    private Double baseCost;
    private Double feePercentage;
    private Double interInstitutionFee;
    private Double totalAmount;
    private Boolean isAvailable = true;
    private String conflictMessage;
}
