// package com.example.ecommerce.repository;

// import com.example.ecommerce.dto.CustomerReportDTO;
// import com.example.ecommerce.dto.SalesOverTimeDTO;
// import com.example.ecommerce.model.Order;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// public interface OrderRepository extends JpaRepository<Order, Integer> {

//     List<Order> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

//     Optional<Order> findByOrderIdAndUser_UserId(Integer orderId, Integer userId);

//     List<Order> findByUser_UserIdAndStatus(Integer userId, String status);

//     @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.orderId = :orderId")
//     Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

//     @Query("SELECT o FROM Order o WHERE CAST(o.orderId AS string) LIKE %:keyword%")
//     Page<Order> searchByOrderIdLike(@Param("keyword") String keyword, Pageable pageable);

//     Page<Order> findByStatus(String status, Pageable pageable);

//     // Truy vấn đơn hàng hoàn tất trong khoảng thời gian
//     @Query("""
//                 SELECT o FROM Order o
//                 WHERE o.status = 'Completed'
//                 AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
//                 AND (:toDate IS NULL OR o.createdAt <= :toDate)
//             """)
//     List<Order> findCompletedOrdersBetweenDates(
//             @Param("fromDate") LocalDate fromDate,
//             @Param("toDate") LocalDate toDate);

//     // Doanh thu theo tháng
//     @Query("""
//                 SELECT new com.example.ecommerce.dto.SalesOverTimeDTO(
//                     YEAR(o.createdAt), MONTH(o.createdAt), SUM(o.totalAmount))
//                 FROM Order o
//                 WHERE o.status = 'Completed'
//                 AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
//                 AND (:toDate IS NULL OR o.createdAt <= :toDate)
//                 GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
//                 ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)
//             """)
//     List<SalesOverTimeDTO> getRevenueGroupedByMonth(
//             @Param("fromDate") LocalDate fromDate,
//             @Param("toDate") LocalDate toDate);

//     // Báo cáo theo khách hàng
//     @Query("""
//             SELECT new com.example.ecommerce.dto.CustomerReportDTO(
//                 o.user.userId,
//                 o.user.fullName,
//                 o.user.email,
//                 COUNT(o),
//                 SUM(o.totalAmount)
//             )
//             FROM Order o
//             WHERE o.status = 'Completed'
//             AND (:from IS NULL OR o.createdAt >= :from)
//             AND (:to IS NULL OR o.createdAt <= :to)
//             GROUP BY o.user.userId, o.user.fullName, o.user.email
//             """)
//     List<CustomerReportDTO> getCustomerReport(
//             @Param("from") LocalDate from,
//             @Param("to") LocalDate to);

// }

package com.example.ecommerce.repository;

import com.example.ecommerce.dto.CustomerReportDTO;
import com.example.ecommerce.dto.SalesOverTimeDTO;
import com.example.ecommerce.model.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    Optional<Order> findByOrderIdAndUser_UserId(Integer orderId, Integer userId);

    List<Order> findByUser_UserIdAndStatus(Integer userId, String status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails od LEFT JOIN FETCH od.product WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

    @Query("SELECT o FROM Order o WHERE CAST(o.orderId AS string) LIKE %:keyword%")
    Page<Order> searchByOrderIdLike(@Param("keyword") String keyword, Pageable pageable);

    Page<Order> findByStatus(String status, Pageable pageable);

    // Truy vấn đơn hàng hoàn tất trong khoảng thời gian
    @Query("""
                SELECT o FROM Order o
                WHERE o.status = 'Completed'
                AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
                AND (:toDate IS NULL OR o.createdAt < :toDate)
                ORDER BY o.createdAt DESC
            """)
    List<Order> findCompletedOrdersBetweenDates(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    // Doanh thu theo tháng
    @Query("""
                SELECT new com.example.ecommerce.dto.SalesOverTimeDTO(
                    YEAR(o.createdAt), MONTH(o.createdAt), COALESCE(SUM(o.totalAmount), 0))
                FROM Order o
                WHERE o.status = 'Completed'
                AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
                AND (:toDate IS NULL OR o.createdAt < :toDate)
                GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
                ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)
            """)
    List<SalesOverTimeDTO> getRevenueGroupedByMonth(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    // Báo cáo theo khách hàng
    @Query("""
            SELECT new com.example.ecommerce.dto.CustomerReportDTO(
                o.user.userId,
                COALESCE(o.user.fullName, 'N/A'),
                COALESCE(o.user.email, 'N/A'),
                COUNT(o),
                COALESCE(SUM(o.totalAmount), 0)
            )
            FROM Order o
            WHERE o.status = 'Completed'
            AND (:from IS NULL OR o.createdAt >= :from)
            AND (:to IS NULL OR o.createdAt < :to)
            GROUP BY o.user.userId, o.user.fullName, o.user.email
            ORDER BY SUM(o.totalAmount) DESC
            """)
    List<CustomerReportDTO> getCustomerReport(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}

