package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    // Quan hệ N-1 với User
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Quan hệ N-1 với Promotion
    @ManyToOne
    @JoinColumn(name = "promotionId")
    private Promotion promotion;

    private BigDecimal discountAmount;

    // Quan hệ 1-N với Invoice
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<Invoice> invoices;

    // Quan hệ 1-N với OrderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

//    // Quan hệ 1-N với Payment
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<Payment> payments;
//
//    // Quan hệ 1-N với Shipment
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<Shipment> shipments;

    // Constructors, getters, setters (hoặc dùng Lombok để tự động sinh)
}
