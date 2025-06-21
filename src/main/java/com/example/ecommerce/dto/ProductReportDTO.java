package com.example.ecommerce.dto;

import java.math.BigDecimal;

public class ProductReportDTO {
    private Long productId;
    private String productName;
    private String category;
    private Long quantitySold;
    private BigDecimal totalRevenue;

    public ProductReportDTO(Integer productId, String productName, String category, Long quantitySold, BigDecimal totalRevenue) {
        this.productId = productId != null ? productId.longValue() : null;
        this.productName = productName;
        this.category = category;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
