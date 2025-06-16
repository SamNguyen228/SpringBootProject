package com.example.ecommerce.model.viewmodel;

import java.util.List;

import com.example.ecommerce.model.Product;

import java.util.ArrayList;

public class ProductsDetailVM {
    private Integer maSP;
    private String tenSP;
    private String moTa;
    private String hinhAnh;
    private Double gia;
    private Integer soLuong;
    private Integer maLoai;
    private String tenLoai;
    private String chiTiet;
    private double diemDanhGia;
    private Integer soLuongTon;
    private String mauSac;
    private String dungLuong;
    
    private List<Product> products;
    private List<ReviewVM> reviews = new ArrayList<>();
    
    private Boolean daMuaHang;
    private Boolean choPhepDanhGia;

    // Getters and Setters

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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
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

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(String chiTiet) {
        this.chiTiet = chiTiet;
    }

    public double getDiemDanhGia() {
        return diemDanhGia;
    }

    public void setDiemDanhGia(double diemDanhGia) {
        this.diemDanhGia = diemDanhGia;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getDungLuong() {
        return dungLuong;
    }

    public void setDungLuong(String dungLuong) {
        this.dungLuong = dungLuong;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<ReviewVM> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewVM> reviews) {
        this.reviews = reviews;
    }

    public Boolean getDaMuaHang() {
        return daMuaHang;
    }

    public void setDaMuaHang(Boolean daMuaHang) {
        this.daMuaHang = daMuaHang;
    }

    public Boolean getChoPhepDanhGia() {
        return choPhepDanhGia;
    }

    public void setChoPhepDanhGia(Boolean choPhepDanhGia) {
        this.choPhepDanhGia = choPhepDanhGia;
    }
}

