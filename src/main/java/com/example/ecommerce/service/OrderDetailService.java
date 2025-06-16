package com.example.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.repository.OrderDetailRepository;

@Service
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public int countPurchasedProductByUser(int productId, int userId) {
        return orderDetailRepository.countPurchasedProductByUser(productId, userId);
    }
}

