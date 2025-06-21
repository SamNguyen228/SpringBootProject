package com.example.ecommerce.dto;

import java.math.BigDecimal;

public class CustomerReportDTO {
   private Integer userId;
    private String name;
    private String email;
    private Long orderCount;
    private BigDecimal totalSpent;

    public CustomerReportDTO(Integer userId, String name, String email, Long orderCount, BigDecimal totalSpent) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
}

