package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private LocalDateTime createdAt;

    // Quan hệ N-1 với Product
    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    // Quan hệ N-1 với User
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // Constructors, getters, setters (hoặc dùng Lombok)
}
