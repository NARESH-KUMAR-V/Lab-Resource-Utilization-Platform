package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.dto.BillingSummaryDTO;
import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.BillingRepository;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.util.SecurityUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillingRepository billingRepository;
    private final BookingRepository bookingRepository;
    private final SharingRequestRepository sharingRequestRepository;
    private final SecurityUtil securityUtil;

    public BillingServiceImpl(
            BillingRepository billingRepository,
            BookingRepository bookingRepository,
            @Lazy SharingRequestRepository sharingRequestRepository,
            SecurityUtil securityUtil) {

        this.billingRepository = billingRepository;
        this.bookingRepository = bookingRepository;
        this.sharingRequestRepository = sharingRequestRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional
    public List<Billing> getAllBillings() {
        User user = securityUtil.getCurrentUser();
        syncBillingsFromBookingsAndSharing();

        if (securityUtil.isSystemAdmin(user)) {
            return billingRepository.findAll();
        }

        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            return billingRepository.findByEquipmentLaboratoryId(user.getLaboratory().getId());
        }

        if (user.getRole() == Role.DEPARTMENT_HEAD && user.getDepartment() != null) {
            return billingRepository.findByDepartmentName(user.getDepartment());
        }

        if (user.getRole() == Role.RESEARCHER || user.getRole() == Role.LAB_TECHNICIAN) {
            return billingRepository.findByUserEmail(user.getEmail());
        }

        Long instId = securityUtil.getUserInstitutionId(user);
        if (instId == null) {
            return List.of();
        }

        return billingRepository.findByInstitutionOrOwningInstitutionId(instId);
    }

    @Override
    @Transactional
    public List<Billing> getMyBillings() {
        User user = securityUtil.getCurrentUser();
        syncBillingsFromBookingsAndSharing();
        return billingRepository.findByUserEmail(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public Billing getBillingById(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing record not found with id: " + id));

        User user = securityUtil.getCurrentUser();

        if (securityUtil.isSystemAdmin(user)) {
            return billing;
        }

        boolean isOwnerUser = billing.getUser() != null && billing.getUser().getId().equals(user.getId());

        if (user.getRole() == Role.RESEARCHER) {
            if (!isOwnerUser) {
                throw new AccessDeniedException("Access denied: You are only authorized to view your own invoices.");
            }
            return billing;
        }

        if (user.getRole() == Role.LAB_MANAGER) {
            Long userLabId = user.getLaboratory() != null ? user.getLaboratory().getId() : null;
            Long billingLabId = billing.getEquipment() != null && billing.getEquipment().getLaboratory() != null
                    ? billing.getEquipment().getLaboratory().getId() : null;

            if (userLabId == null || !userLabId.equals(billingLabId)) {
                throw new AccessDeniedException("Access denied: You can only view invoices for equipment belonging to your laboratory.");
            }
            return billing;
        }

        if (user.getRole() == Role.DEPARTMENT_HEAD) {
            String userDept = user.getDepartment();
            String billingDept = billing.getDepartment();
            String labDept = billing.getEquipment() != null && billing.getEquipment().getLaboratory() != null
                    ? billing.getEquipment().getLaboratory().getDepartment() : null;

            boolean deptMatches = userDept != null && (userDept.equalsIgnoreCase(billingDept) || userDept.equalsIgnoreCase(labDept));
            if (!deptMatches) {
                throw new AccessDeniedException("Access denied: You can only view invoices belonging to your department.");
            }
            return billing;
        }

        Long userInstId = securityUtil.getUserInstitutionId(user);
        boolean isReqInst = billing.getInstitution() != null && userInstId != null && billing.getInstitution().getId().equals(userInstId);
        boolean isOwnInst = billing.getOwningInstitution() != null && userInstId != null && billing.getOwningInstitution().getId().equals(userInstId);

        if (!isOwnerUser && !isReqInst && !isOwnInst) {
            throw new AccessDeniedException("Access denied: You are not authorized to view this invoice.");
        }

        return billing;
    }

    @Override
    public Billing generateBillingFromBooking(Booking booking) {
        Optional<Billing> existing = billingRepository.findByBookingId(booking.getId());
        if (existing.isPresent()) {
            Billing b = existing.get();
            if (booking.getStatus() == BookingStatus.COMPLETED) {
                b.setBillingStatus(BillingStatus.CLOSED);
                return billingRepository.save(b);
            }
            return b;
        }

        Equipment equipment = booking.getEquipment();
        User user = booking.getUser();

        long days = booking.getStartDate().until(booking.getEndDate()).getDays() + 1;
        double costPerDay = equipment.getCostPerDay() != null ? equipment.getCostPerDay() : 500.0;
        double baseCost = days * costPerDay;

        double interInstFee = 0.0;
        Institution userInst = user.getInstitution();
        Institution eqInst = equipment.getLaboratory() != null ? equipment.getLaboratory().getInstitution() : null;

        if (userInst != null && eqInst != null && !userInst.getId().equals(eqInst.getId())) {
            interInstFee = Math.round(baseCost * 0.15 * 100.0) / 100.0;
        }

        double totalAmount = baseCost + interInstFee;

        Billing billing = new Billing();
        billing.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + String.format("%05d", booking.getId()));
        billing.setBooking(booking);
        billing.setUser(user);
        billing.setEquipment(equipment);
        billing.setInstitution(userInst != null ? userInst : eqInst);
        billing.setOwningInstitution(eqInst);
        billing.setDepartment(equipment.getLaboratory() != null && equipment.getLaboratory().getDepartment() != null
                ? equipment.getLaboratory().getDepartment()
                : (user.getDepartment() != null ? user.getDepartment() : "General Research"));
        billing.setUsageDays((double) days);
        billing.setCostPerDay(costPerDay);
        billing.setEstimatedCost(baseCost);
        billing.setInterInstitutionFee(interInstFee);
        billing.setTotalAmount(totalAmount);
        billing.setInvoiceDate(LocalDate.now());

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            billing.setBillingStatus(BillingStatus.CLOSED);
        } else if (booking.getStatus() == BookingStatus.APPROVED) {
            billing.setBillingStatus(BillingStatus.GENERATED);
        } else {
            billing.setBillingStatus(BillingStatus.ESTIMATED);
        }

        return billingRepository.save(billing);
    }

    @Override
    public Billing generateBillingFromSharingRequest(SharingRequest sharingRequest) {
        if (sharingRequest == null || sharingRequest.getId() == null) return null;

        Optional<Billing> existing = billingRepository.findBySharingRequestId(sharingRequest.getId());
        if (existing.isPresent()) {
            Billing billing = existing.get();
            if ("COMPLETED".equalsIgnoreCase(sharingRequest.getStatus())) {
                billing.setBillingStatus(BillingStatus.CLOSED);
            } else if ("APPROVED".equalsIgnoreCase(sharingRequest.getStatus()) || "ACTIVE".equalsIgnoreCase(sharingRequest.getStatus())) {
                billing.setBillingStatus(BillingStatus.GENERATED);
            }
            return billingRepository.save(billing);
        }

        Equipment equipment = sharingRequest.getEquipment();
        User user = sharingRequest.getRequester();
        if (equipment == null || user == null) return null;

        long days = sharingRequest.getStartDate() != null && sharingRequest.getEndDate() != null
                ? sharingRequest.getStartDate().until(sharingRequest.getEndDate()).getDays() + 1 : 1;
        double costPerDay = equipment.getCostPerDay() != null ? equipment.getCostPerDay() : 2000.0;
        double baseCost = days * costPerDay;
        double interInstFee = Math.round(baseCost * 0.10 * 100.0) / 100.0;
        double totalAmount = baseCost + interInstFee;

        Institution userInst = user.getInstitution() != null ? user.getInstitution()
                : (user.getLaboratory() != null ? user.getLaboratory().getInstitution() : null);
        Institution eqInst = equipment.getLaboratory() != null ? equipment.getLaboratory().getInstitution() : null;

        Billing billing = new Billing();
        billing.setInvoiceNumber("INV-SHARING-" + LocalDate.now().getYear() + "-" + String.format("%05d", sharingRequest.getId()));
        billing.setSharingRequest(sharingRequest);
        billing.setUser(user);
        billing.setEquipment(equipment);
        billing.setInstitution(userInst != null ? userInst : eqInst);
        billing.setOwningInstitution(eqInst);
        billing.setDepartment(equipment.getLaboratory() != null && equipment.getLaboratory().getDepartment() != null
                ? equipment.getLaboratory().getDepartment()
                : (user.getDepartment() != null ? user.getDepartment() : "Collaborative Research"));
        billing.setUsageDays((double) days);
        billing.setCostPerDay(costPerDay);
        billing.setEstimatedCost(baseCost);
        billing.setInterInstitutionFee(interInstFee);
        billing.setTotalAmount(totalAmount);
        billing.setInvoiceDate(LocalDate.now());

        if ("COMPLETED".equalsIgnoreCase(sharingRequest.getStatus())) {
            billing.setBillingStatus(BillingStatus.CLOSED);
        } else if ("APPROVED".equalsIgnoreCase(sharingRequest.getStatus()) || "ACTIVE".equalsIgnoreCase(sharingRequest.getStatus())) {
            billing.setBillingStatus(BillingStatus.GENERATED);
        } else {
            billing.setBillingStatus(BillingStatus.ESTIMATED);
        }

        return billingRepository.save(billing);
    }

    @Override
    public Billing updateBillingStatus(Long id, String status) {
        Billing billing = getBillingById(id);
        User user = securityUtil.getCurrentUser();

        if (user.getRole() == Role.RESEARCHER) {
            throw new AccessDeniedException("Access denied: Researchers cannot update billing statuses");
        }

        BillingStatus newStatus = BillingStatus.valueOf(status.toUpperCase());
        billing.setBillingStatus(newStatus);
        return billingRepository.save(billing);
    }

    @Override
    @Transactional
    public BillingSummaryDTO getBillingSummary() {
        User user = securityUtil.getCurrentUser();
        syncBillingsFromBookingsAndSharing();

        List<Billing> records = getAllBillings();
        BillingSummaryDTO summary = new BillingSummaryDTO();

        double totalEstimated = records.stream().mapToDouble(b -> b.getEstimatedCost() != null ? b.getEstimatedCost() : 0.0).sum();
        double totalInterFees = records.stream().mapToDouble(b -> b.getInterInstitutionFee() != null ? b.getInterInstitutionFee() : 0.0).sum();
        double totalBilled = records.stream().mapToDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0).sum();

        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null) {
            Long labId = user.getLaboratory().getId();
            Double outgoing = billingRepository.getOutgoingSharingCostByLaboratory(labId);
            Double revenue = billingRepository.getResourceSharingValueByLaboratory(labId);
            summary.setOutgoingSharingCost(outgoing != null ? outgoing : 0.0);
            summary.setResourceSharingValue(revenue != null ? revenue : 0.0);
        } else {
            Long instId = securityUtil.getUserInstitutionId(user);
            if (instId != null) {
                Double outgoing = billingRepository.getOutgoingSharingCostByInstitution(instId);
                Double revenue = billingRepository.getResourceSharingValueByInstitution(instId);
                summary.setOutgoingSharingCost(outgoing != null ? outgoing : 0.0);
                summary.setResourceSharingValue(revenue != null ? revenue : 0.0);
            }
        }

        summary.setTotalEstimatedCost(totalEstimated);
        summary.setTotalInterInstitutionFees(totalInterFees);
        summary.setTotalBilledAmount(totalBilled);
        summary.setTotalInvoicesCount((long) records.size());

        long genCount = records.stream().filter(b -> b.getBillingStatus() == BillingStatus.GENERATED).count();
        long closedCount = records.stream().filter(b -> b.getBillingStatus() == BillingStatus.CLOSED).count();

        summary.setGeneratedInvoicesCount(genCount);
        summary.setClosedInvoicesCount(closedCount);

        Map<String, Double> deptSummary = records.stream()
                .filter(b -> b.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        Billing::getDepartment,
                        Collectors.summingDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                ));

        Map<String, Double> instSummary = records.stream()
                .filter(b -> b.getInstitution() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getInstitution().getName(),
                        Collectors.summingDouble(b -> b.getTotalAmount() != null ? b.getTotalAmount() : 0.0)
                ));

        summary.setDepartmentCostSummary(deptSummary);
        summary.setInstitutionCostSummary(instSummary);

        return summary;
    }

    private void syncBillingsFromBookingsAndSharing() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            for (Booking booking : bookings) {
                if (booking.getStatus() == BookingStatus.APPROVED ||
                    booking.getStatus() == BookingStatus.COMPLETED ||
                    booking.getStatus() == BookingStatus.PENDING) {
                    generateBillingFromBooking(booking);
                }
            }

            List<SharingRequest> sharingRequests = sharingRequestRepository.findAll();
            for (SharingRequest req : sharingRequests) {
                if ("APPROVED".equalsIgnoreCase(req.getStatus()) ||
                    "ACTIVE".equalsIgnoreCase(req.getStatus()) ||
                    "COMPLETED".equalsIgnoreCase(req.getStatus())) {
                    generateBillingFromSharingRequest(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
