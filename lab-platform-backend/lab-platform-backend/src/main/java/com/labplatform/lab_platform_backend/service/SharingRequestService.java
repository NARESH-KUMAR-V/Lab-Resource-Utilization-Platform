package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.SharingRequest;

import java.util.List;

public interface SharingRequestService {

    List<SharingRequest> getAllRequests();

    SharingRequest getRequestById(Long id);

    SharingRequest createRequest(SharingRequest request, String userEmail);

    List<SharingRequest> getMyRequests(String userEmail);

    SharingRequest approveRequest(Long id);

    SharingRequest rejectRequest(Long id);

    void deleteRequest(Long id);
}