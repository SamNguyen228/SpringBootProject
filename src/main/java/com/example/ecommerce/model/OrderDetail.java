package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Order_Details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    // Quan hệ N-1 với Order
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    // Quan hệ N-1 với Product
    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    // Constructors, getters, setters (hoặc dùng Lombok)
}
