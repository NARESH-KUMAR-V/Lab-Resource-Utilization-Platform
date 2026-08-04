package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.BillingSummaryDTO;
import com.labplatform.lab_platform_backend.entity.Billing;
import com.labplatform.lab_platform_backend.service.BillingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<Billing> getAllBillings() {
        return billingService.getAllBillings();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public List<Billing> getMyBillings() {
        return billingService.getMyBillings();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RESEARCHER') or hasRole('LAB_TECHNICIAN') or hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Billing getBillingById(@PathVariable Long id) {
        return billingService.getBillingById(id);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public BillingSummaryDTO getBillingSummary() {
        return billingService.getBillingSummary();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('LAB_MANAGER') or hasRole('DEPARTMENT_HEAD') or hasRole('INSTITUTION_ADMIN') or hasRole('SYSTEM_ADMIN')")
    public Billing updateBillingStatus(@PathVariable Long id, @RequestParam String status) {
        return billingService.updateBillingStatus(id, status);
    }
}
