package com.example.ecommerce.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.dto.ProductReportDTO;
import com.example.ecommerce.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.product.id = :productId AND od.order.user.id = :userId")
    int countPurchasedProductByUser(int productId, int userId);

    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.product WHERE od.order.orderId = :orderId")
    List<OrderDetail> findByOrderIdWithProduct(@Param("orderId") Integer orderId);

    List<OrderDetail> findByOrder_OrderId(Integer orderId);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.order.id IN :orderIds")
    Integer sumQuantityByOrderIds(@Param("orderIds") List<Integer> orderIds);

    @Query("SELECT od.product.category.categoryName, SUM(od.quantity) " +
        "FROM OrderDetail od WHERE od.order.id IN :orderIds " +
        "GROUP BY od.product.category.categoryName")
    List<Object[]> getProductSoldByCategoryRaw(@Param("orderIds") List<Integer> orderIds);

    default Map<String, Integer> getProductSoldByCategory(List<Integer> orderIds) {
        List<Object[]> raw = getProductSoldByCategoryRaw(orderIds);
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Object[] obj : raw) {
            map.put((String) obj[0], ((Long) obj[1]).intValue());
        }
        return map;
    }

   @Query("""
    SELECT new com.example.ecommerce.dto.ProductReportDTO(
        od.product.productId,
        od.product.productName,
        od.product.category.categoryName,
        SUM(od.quantity),
        CAST(SUM(od.quantity * od.price) AS big_decimal)
    )
    FROM OrderDetail od
    WHERE od.order.orderId IN :orderIds
    GROUP BY od.product.id, od.product.productName, od.product.category.categoryName
    """)
    List<ProductReportDTO> getProductSalesReport(@Param("orderIds") List<Integer> orderIds);
}
