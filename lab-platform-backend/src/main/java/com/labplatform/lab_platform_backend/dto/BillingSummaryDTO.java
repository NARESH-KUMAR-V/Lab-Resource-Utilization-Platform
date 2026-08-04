package com.labplatform.lab_platform_backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BillingSummaryDTO {
    private Double totalEstimatedCost;
    private Double totalInterInstitutionFees;
    private Double totalBilledAmount;
    private Double outgoingSharingCost = 0.0;
    private Double resourceSharingValue = 0.0;
    private Long totalInvoicesCount;
    private Long generatedInvoicesCount;
    private Long closedInvoicesCount;
    private Map<String, Double> departmentCostSummary;
    private Map<String, Double> institutionCostSummary;
}
