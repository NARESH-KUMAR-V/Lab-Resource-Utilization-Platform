package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Existing
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    long countByIsReadFalse();

    // Researcher Dashboard
    long countByUserEmailAndIsReadFalse(String email);

}