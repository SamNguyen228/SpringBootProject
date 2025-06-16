package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
} 