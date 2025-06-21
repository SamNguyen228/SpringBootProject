package com.example.ecommerce.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ecommerce.model.Notification;
import com.example.ecommerce.repository.NotificationRepository;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    @ResponseBody
    public Map<String, Object> getAdminNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(thirtyDaysAgo);
        if (!oldNotifications.isEmpty()) {
            notificationRepository.deleteAll(oldNotifications);
        }

        List<Notification> recentNotifications = notificationRepository.findByCreatedAtAfterOrderByCreatedAtDesc(thirtyDaysAgo);

        List<Map<String, String>> data = recentNotifications.stream()
            .map(n -> Map.of(
                "message", n.getContent(),
                "createdAt", n.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ))
            .collect(Collectors.toList());

        return Map.of(
            "count", data.size(),
            "data", data
        );
    }
}
