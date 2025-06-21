package com.example.ecommerce.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.ecommerce.model.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return user.getUserId();
    }

    public String getFullName() {
        return user.getFullName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_CUSTOMER";

        if (user.getRoleId() == 1) {
            roleName = "ROLE_ADMIN";
        }
        
        String role = switch (user.getRoleId()) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_CUSTOMER";
            default -> "ROLE_CUSTOMER";
        };
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAccountNonExpired() { return true; }
    public boolean isCredentialsNonExpired() { return true; }

}
