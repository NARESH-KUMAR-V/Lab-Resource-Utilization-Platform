package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Notification;
import com.labplatform.lab_platform_backend.entity.Role;
import com.labplatform.lab_platform_backend.entity.SharingRequest;
import com.labplatform.lab_platform_backend.entity.User;
import com.labplatform.lab_platform_backend.entity.UserStatus;
import com.labplatform.lab_platform_backend.repository.NotificationRepository;
import com.labplatform.lab_platform_backend.repository.SharingRequestRepository;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SharingRequestRepository sharingRequestRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            @Lazy UserRepository userRepository,
            @Lazy SharingRequestRepository sharingRequestRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sharingRequestRepository = sharingRequestRepository;
    }

    @Override
    public List<Notification> getMyNotifications(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getRole() == Role.SYSTEM_ADMIN) {
                syncSystemAdminNotifications(user);
            }
        }
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Override
    public Notification createNotification(User user, String message) {
        if (user == null || message == null) return null;

        // Prevent duplicate creation of identical notification
        if (notificationRepository.existsByUserIdAndMessage(user.getId(), message)) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    @Override
    public void notifySystemAdmins(String message) {
        if (message == null) return;
        List<User> admins = userRepository.findByRole(Role.SYSTEM_ADMIN);
        for (User admin : admins) {
            createNotification(admin, message);
        }
    }

    @Override
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    public boolean existsNotification(User user, String message) {
        if (user == null || user.getId() == null || message == null) return false;
        return notificationRepository.existsByUserIdAndMessage(user.getId(), message);
    }

    private void syncSystemAdminNotifications(User admin) {
        try {
            // 1. Pending User Approvals Audit Notifications
            List<User> pendingUsers = userRepository.findByStatus(UserStatus.PENDING);
            for (User pending : pendingUsers) {
                String msg = "Pending User Approval: " + pending.getName() + " (" + pending.getEmail() + ") requested role " + pending.getRole() + ". Action required in User Approval portal.";
                createNotification(admin, msg);
            }

            // 2. Active Inter-Institution Sharing Notifications
            List<SharingRequest> sharingRequests = sharingRequestRepository.findAll();
            for (SharingRequest req : sharingRequests) {
                String eqName = req.getEquipment() != null ? req.getEquipment().getName() : "Equipment";
                String msg = "Inter-Institution Sharing Audit: Request #" + req.getId() + " for " + eqName + " (" + req.getRequestingInstitution() + ") status is " + req.getStatus() + ".";
                createNotification(admin, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}