package com.example.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.model.viewmodel.CartItem;
import com.example.ecommerce.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    // public List<CartItem> getCartItemsByUserId(Integer userId) {
    //     return cartRepository.findCartItemsByUserId(userId);
    // }
}

