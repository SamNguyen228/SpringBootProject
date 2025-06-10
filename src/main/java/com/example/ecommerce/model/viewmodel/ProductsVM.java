package com.example.ecommerce.model.viewmodel;

import java.math.BigDecimal;

public class ProductsVM {
    private Integer maLoai;
    private Integer maSP;
    private String tenSP;
    private String hinhAnh;
    private Double gia;
    private String moTa;
    private String tenLoai;

    // Constructor đầy đủ
    public ProductsVM(Integer maLoai, Integer maSP, String tenSP, String hinhAnh, BigDecimal gia, String moTa, String tenLoai) {
        this.maLoai = maLoai;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.hinhAnh = hinhAnh;
        this.gia = (gia != null) ? gia.doubleValue() : null;
        this.moTa = moTa;
        this.tenLoai = tenLoai;
    }

    public ProductsVM() {
        // Constructor mặc định (nếu cần)
    }

    // Getter và Setter
    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

    public Integer getMaSP() {
        return maSP;
    }

    public void setMaSP(Integer maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }
}
