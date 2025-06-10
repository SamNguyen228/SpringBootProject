package com.example.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    @Column(nullable = false)
    private Integer quantity;

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
