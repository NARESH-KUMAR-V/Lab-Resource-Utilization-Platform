package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.service.SharingRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sharing-requests")
public class SharingRequestController {

    private final SharingRequestService sharingRequestService;

    public SharingRequestController(SharingRequestService sharingRequestService) {
        this.sharingRequestService = sharingRequestService;
    }

    @GetMapping
    public List<SharingRequest> getAllRequests() {
        return sharingRequestService.getAllRequests();
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