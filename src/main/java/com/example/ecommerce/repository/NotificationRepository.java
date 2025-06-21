package com.example.ecommerce.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime fromDate);
    List<Notification> findByCreatedAtBefore(LocalDateTime beforeDate);
} 