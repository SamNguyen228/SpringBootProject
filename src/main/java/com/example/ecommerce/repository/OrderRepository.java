package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.model.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Order> findByOrderIdAndUser_UserId(Integer orderId, Integer userId);

    List<Order> findByUser_UserIdAndStatus(Integer userId, String status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);
}

