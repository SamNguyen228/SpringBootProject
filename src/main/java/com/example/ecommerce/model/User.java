package com.example.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "roleId", insertable = false, updatable = false)
    private Integer roleId;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, message = "Tên phải có ít nhất 2 ký tự")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).{6,}$",
        message = "Mật khẩu phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    private String passwordHash;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    private LocalDateTime createdAt;
    private Boolean isLocked;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordExpiry;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // === Getters ===
    public Integer getUserId() {
        return userId;
    }

    public Integer getRoleId() {
        return (role != null) ? role.getRoleId() : roleId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public boolean isLocked() {
        return Boolean.TRUE.equals(isLocked);
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public LocalDateTime getResetPasswordExpiry() {
        return resetPasswordExpiry;
    }

    public Role getRole() {
        return role;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    // === Setters ===
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public void setResetPasswordExpiry(LocalDateTime resetPasswordExpiry) {
        this.resetPasswordExpiry = resetPasswordExpiry;
    }

    public void setRole(Role role) {
        this.role = role;
        if (role != null) {
            this.roleId = role.getRoleId();
        }
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
