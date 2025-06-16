package com.example.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer > {
    List<Cart> findByUser_UserId(Integer userId);

    Optional<Cart> findByUser_UserIdAndProductId(Integer userId, Integer productId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.userId = :userId")
    List<Cart> findByUserIdWithProduct(@Param("userId") Integer userId);

    void deleteAllByUserId(Integer userId);
}

