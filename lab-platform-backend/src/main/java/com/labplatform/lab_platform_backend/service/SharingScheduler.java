package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Billing;
import com.labplatform.lab_platform_backend.entity.BillingStatus;
import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.EquipmentStatus;
import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.repository.BillingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class SharingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SharingScheduler.class);

    private final SharingRequestRepository sharingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationService notificationService;
    private final EquipmentService equipmentService;
    private final BillingRepository billingRepository;

    public SharingScheduler(
            SharingRequestRepository sharingRequestRepository,
            EquipmentRepository equipmentRepository,
            NotificationService notificationService,
            EquipmentService equipmentService,
            BillingRepository billingRepository) {
        this.sharingRequestRepository = sharingRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationService = notificationService;
        this.equipmentService = equipmentService;
        this.billingRepository = billingRepository;
    }

    // Runs every 5 minutes and on application startup
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void processSharingLifecycle() {
        LocalDate today = LocalDate.now();

        // 1. Activate APPROVED requests whose start date has arrived
        List<SharingRequest> toActivate = sharingRequestRepository.findRequestsToActivate(today);
        for (SharingRequest req : toActivate) {
            req.setStatus("ACTIVE");
            sharingRequestRepository.save(req);

            Equipment eq = req.getEquipment();
            if (eq != null) {
                eq.setStatus(EquipmentStatus.SHARED);
                equipmentRepository.save(eq);
            }

            notificationService.createNotification(
                    req.getRequester(),
                    "Your approved sharing period for " + (eq != null ? eq.getName() : "equipment") + " has started."
            );
            logger.info("Sharing request #{} activated for equipment: {}", req.getId(), eq != null ? eq.getName() : "N/A");
        }

        // 2. Complete ACTIVE / APPROVED requests whose end date has passed
        List<SharingRequest> toComplete = sharingRequestRepository.findRequestsToComplete(today);
        for (SharingRequest req : toComplete) {
            req.setStatus("COMPLETED");
            sharingRequestRepository.save(req);

            Equipment eq = req.getEquipment();
            if (eq != null) {
                equipmentService.recalculateEquipmentStatus(eq);
            }

            // Update dummy billing status to CLOSED
            Optional<Billing> optBilling = billingRepository.findBySharingRequestId(req.getId());
            if (optBilling.isPresent()) {
                Billing billing = optBilling.get();
                billing.setBillingStatus(BillingStatus.CLOSED);
                billingRepository.save(billing);
            }

            notificationService.createNotification(
                    req.getRequester(),
                    "The sharing period for " + (eq != null ? eq.getName() : "equipment") + " has been completed."
            );
            logger.info("Sharing request #{} completed for equipment: {}", req.getId(), eq != null ? eq.getName() : "N/A");
        }
    }
}
