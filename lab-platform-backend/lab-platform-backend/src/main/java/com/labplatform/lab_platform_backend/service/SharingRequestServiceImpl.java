package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Equipment;
import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.repository.EquipmentRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SharingRequestServiceImpl implements SharingRequestService {

    private final SharingRequestRepository sharingRequestRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SharingRequestServiceImpl(
            SharingRequestRepository sharingRequestRepository,
            EquipmentRepository equipmentRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.sharingRequestRepository = sharingRequestRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<SharingRequest> getAllRequests() {
        return sharingRequestRepository.findAll();
    }

    @Override
    public SharingRequest getRequestById(Long id) {
        return sharingRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sharing request not found"));
    }

    @Override
    public SharingRequest createRequest(SharingRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Equipment equipment = equipmentRepository.findById(request.getEquipment().getId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.getShared()) {
            throw new RuntimeException("This equipment is not available for sharing.");
        }

        request.setRequester(user);
        request.setEquipment(equipment);
        request.setRequestDate(LocalDate.now());
        request.setStatus("PENDING");
        request.setRequestingInstitution("Unknown");

        return sharingRequestRepository.save(request);
    }

    @Override
    public List<SharingRequest> getMyRequests(String userEmail) {
        return sharingRequestRepository.findByRequesterEmail(userEmail);
    }

    @Override
    public SharingRequest approveRequest(Long id) {

        SharingRequest request = getRequestById(id);

        request.setStatus("APPROVED");

        notificationService.createNotification(
                request.getRequester(),
                "Your sharing request for "
                        + request.getEquipment().getName()
                        + " has been approved."
        );

        return sharingRequestRepository.save(request);
    }

    @Override
    public SharingRequest rejectRequest(Long id) {

        SharingRequest request = getRequestById(id);

        request.setStatus("REJECTED");

        notificationService.createNotification(
                request.getRequester(),
                "Your sharing request for "
                        + request.getEquipment().getName()
                        + " has been rejected."
        );

        return sharingRequestRepository.save(request);
    }

    @Override
    public void deleteRequest(Long id) {

        SharingRequest request = getRequestById(id);

        sharingRequestRepository.delete(request);
    }
}