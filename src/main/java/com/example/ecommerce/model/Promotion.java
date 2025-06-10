package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promotionId;

    @Column(nullable = false)
    private String promotionName;

    private BigDecimal discountPercentage;

    private BigDecimal discountAmount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscount;

    private LocalDateTime createdAt;

    // Quan hệ 1-N với Order
    @OneToMany(mappedBy = "promotion")
    private List<Order> orders;

    // Constructors, getters, setters (hoặc dùng Lombok)
}
