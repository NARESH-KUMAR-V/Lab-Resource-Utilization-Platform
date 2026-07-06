package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.SharingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharingRequestRepository extends JpaRepository<SharingRequest, Long> {

    List<SharingRequest> findByRequesterEmail(String email);
    long countByStatus(String status);
}