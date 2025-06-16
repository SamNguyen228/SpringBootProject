package com.example.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.product.id = :productId AND od.order.user.id = :userId")
    int countPurchasedProductByUser(int productId, int userId);

    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product WHERE od.order.orderId = :orderId")
    List<OrderDetail> findByOrderIdWithProduct(@Param("orderId") Integer orderId);

    List<OrderDetail> findByOrder_OrderId(Integer orderId);
}
