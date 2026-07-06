package com.labplatform.lab_platform_backend.controller;

import com.labplatform.lab_platform_backend.entity.Notification;
import com.labplatform.lab_platform_backend.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {

        return notificationService.getMyNotifications(authentication.getName());

    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id) {

        return notificationService.markAsRead(id);

    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {

        notificationService.deleteNotification(id);

    }
}