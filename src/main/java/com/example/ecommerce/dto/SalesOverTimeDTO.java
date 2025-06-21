package com.example.ecommerce.dto;

import java.math.BigDecimal;

public class SalesOverTimeDTO {
    private int year;
    private int month;
    private BigDecimal revenue;

    public SalesOverTimeDTO(int year, int month, BigDecimal revenue) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
    }

    // Getters and Setters
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
