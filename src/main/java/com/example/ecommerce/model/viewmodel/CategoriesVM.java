package com.example.ecommerce.model.viewmodel;

public class CategoriesVM {
    private int maLoai;
    private String tenLoai;
    private int soLuong;

    public CategoriesVM() {
    }

    public CategoriesVM(int maLoai, String tenLoai, int soLuong) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.soLuong = soLuong;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}

