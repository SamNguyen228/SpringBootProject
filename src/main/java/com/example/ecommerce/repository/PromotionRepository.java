package com.example.ecommerce.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT p FROM Promotion p WHERE p.promotionId = :id AND :now BETWEEN p.startDate AND p.endDate")
    Promotion findValidPromotion(@Param("id") Integer id, @Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.promotionName = :code AND :now BETWEEN p.startDate AND p.endDate")
    Promotion findValidByCode(@Param("code") String code, @Param("now") LocalDateTime now);
}
