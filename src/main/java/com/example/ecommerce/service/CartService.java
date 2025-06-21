package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.repository.CartRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getCartItemsByUserId(Integer userId) {
        return cartRepository.findByUser_UserId(userId);
    }

    public int getCartTotalQuantity(List<Cart> cartItems) {
        return cartItems.stream().mapToInt(Cart::getQuantity).sum();
    }

    public double getCartTotalAmount(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}

