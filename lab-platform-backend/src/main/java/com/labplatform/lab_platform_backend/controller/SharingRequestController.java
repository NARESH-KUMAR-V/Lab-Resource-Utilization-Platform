package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.dto.SharingCostEstimateDTO;
import com.labplatform.lab_platform_backend.entity.Booking;
import com.labplatform.lab_platform_backend.entity.BookingStatus;
import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.repository.BookingRepository;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.service.SharingRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sharing-requests")
public class SharingRequestController {

    private final SharingRequestService sharingRequestService;
    private final EquipmentRepository equipmentRepository;
    private final SharingRequestRepository sharingRequestRepository;
    private final BookingRepository bookingRepository;

    public SharingRequestController(
            SharingRequestService sharingRequestService,
            EquipmentRepository equipmentRepository,
            SharingRequestRepository sharingRequestRepository,
            BookingRepository bookingRepository) {
        this.sharingRequestService = sharingRequestService;
        this.equipmentRepository = equipmentRepository;
        this.sharingRequestRepository = sharingRequestRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<SharingRequest> getAllRequests() {
        return sharingRequestService.getAllRequests();
    }

    @GetMapping("/incoming")
    public List<SharingRequest> getIncomingRequests() {
        return sharingRequestService.getIncomingRequests();
    }

    @GetMapping("/outgoing")
    public List<SharingRequest> getOutgoingRequests() {
        return sharingRequestService.getOutgoingRequests();
    }

    @GetMapping("/estimate-cost")
    public SharingCostEstimateDTO getEstimateCost(
            @RequestParam Long equipmentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        long days = start.until(end).getDays() + 1;
        double costPerDay = equipment.getCostPerDay() != null ? equipment.getCostPerDay() : 2000.0;
        double baseCost = days * costPerDay;
        double feePercentage = 10.0;
        double interInstFee = Math.round(baseCost * 0.10 * 100.0) / 100.0;
        double totalCost = baseCost + interInstFee;

        SharingCostEstimateDTO estimate = new SharingCostEstimateDTO();
        estimate.setEquipmentId(equipment.getId());
        estimate.setEquipmentName(equipment.getName());
        estimate.setCostPerDay(costPerDay);
        estimate.setStartDate(start);
        estimate.setEndDate(end);
        estimate.setDurationDays(days);
        estimate.setBaseCost(baseCost);
        estimate.setFeePercentage(feePercentage);
        estimate.setInterInstitutionFee(interInstFee);
        estimate.setTotalAmount(totalCost);

        // Check availability
        List<SharingRequest> overlappingSharing = sharingRequestRepository.findOverlappingSharingRequests(
                equipment.getId(), start, end
        );
        List<Booking> overlappingBookings = bookingRepository.findByEquipmentIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                equipment.getId(), List.of(BookingStatus.APPROVED), end, start
        );

        if (!overlappingSharing.isEmpty()) {
            SharingRequest conflict = overlappingSharing.get(0);
            estimate.setIsAvailable(false);
            estimate.setConflictMessage("Requested sharing period (" + start + " to " + end + ") collides with an existing approved sharing request (" + conflict.getStartDate() + " to " + conflict.getEndDate() + "). These dates are already reserved.");
        } else if (!overlappingBookings.isEmpty()) {
            Booking conflict = overlappingBookings.get(0);
            estimate.setIsAvailable(false);
            estimate.setConflictMessage("Requested sharing period (" + start + " to " + end + ") collides with an approved booking (" + conflict.getStartDate() + " to " + conflict.getEndDate() + "). These dates are already booked.");
        } else {
            estimate.setIsAvailable(true);
            estimate.setConflictMessage(null);
        }

        return estimate;
    }

    @GetMapping("/my")
    public List<SharingRequest> getMyRequests(Authentication authentication) {
        return sharingRequestService.getMyRequests(authentication.getName());
    }

    @PostMapping
    public SharingRequest createRequest(@RequestBody SharingRequest request,
                                         Authentication authentication) {
        return sharingRequestService.createRequest(
                request,
                authentication.getName()
        );
    }

    @PutMapping("/{id}/approve")
    public SharingRequest approveRequest(@PathVariable Long id) {
        return sharingRequestService.approveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public SharingRequest rejectRequest(@PathVariable Long id) {
        return sharingRequestService.rejectRequest(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable Long id) {
        sharingRequestService.deleteRequest(id);
    }
}