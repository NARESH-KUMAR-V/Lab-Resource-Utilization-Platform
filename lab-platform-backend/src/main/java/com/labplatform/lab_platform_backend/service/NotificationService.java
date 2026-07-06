package com.labplatform.lab_platform_backend.service;

import com.labplatform.lab_platform_backend.entity.Notification;
import com.labplatform.lab_platform_backend.entity.User;

import java.util.List;

public interface NotificationService {

    List<Notification> getMyNotifications(String email);

    Notification createNotification(User user, String message);

    Notification markAsRead(Long id);

    void deleteNotification(Long id);
}