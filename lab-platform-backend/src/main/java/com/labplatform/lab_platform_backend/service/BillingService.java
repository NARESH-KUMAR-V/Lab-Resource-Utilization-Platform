package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BillingSummaryDTO;
import com.labplatform.lab_platform_backend.entity.Billing;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.SharingRequest;

import java.util.List;

public interface BillingService {

    List<Billing> getAllBillings();

    List<Billing> getMyBillings();

    Billing getBillingById(Long id);

    Billing generateBillingFromBooking(Booking booking);

    Billing generateBillingFromSharingRequest(SharingRequest sharingRequest);

    Billing updateBillingStatus(Long id, String status);

    BillingSummaryDTO getBillingSummary();
}
