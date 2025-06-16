package com.example.ecommerce.model.viewmodel;

public class CartItem {
    private int maSP;
    private String hinhAnh;
    private String tenSP;
    private double gia;
    private int soLuong;

    public CartItem() {
    }

    public CartItem(int maSP, String hinhAnh, String tenSP, double gia, int soLuong) {
        this.maSP = maSP;
        this.hinhAnh = hinhAnh;
        this.tenSP = tenSP;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    public int getMaSP() {
        return maSP;
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return gia * soLuong;
    }
}

