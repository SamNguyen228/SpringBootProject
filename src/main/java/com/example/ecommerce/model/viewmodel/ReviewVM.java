package com.example.ecommerce.model.viewmodel;

import java.time.LocalDateTime;

public class ReviewVM {
    private Integer maReview;
    private Integer maKhachHang;
    private String tenKhachHang;
    private Integer maSanPham;
    private Integer danhGia;
    private String noiDung;
    private LocalDateTime ngayDang;

    // Getters and Setters
    public Integer getMaReview() {
        return maReview;
    }

    public void setMaReview(Integer maReview) {
        this.maReview = maReview;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public Integer getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(Integer maSanPham) {
        this.maSanPham = maSanPham;
    }

    public Integer getDanhGia() {
        return danhGia;
    }

    public void setDanhGia(Integer danhGia) {
        this.danhGia = danhGia;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(LocalDateTime ngayDang) {
        this.ngayDang = ngayDang;
    }
}
