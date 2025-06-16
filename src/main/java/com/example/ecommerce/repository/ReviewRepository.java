package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProduct_ProductId(Integer productId);
    long countByProduct_ProductIdAndUser_UserId(Integer productId, Integer userId);
    List<Review> findByProduct_ProductIdOrderByCreatedAtDesc(int productId);
    List<Review> findByProduct_ProductIdAndUser_UserId(int productId, int userId);
}

