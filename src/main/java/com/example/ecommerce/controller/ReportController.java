package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomerReportDTO;
import com.example.ecommerce.dto.ProductReportDTO;
import com.example.ecommerce.dto.SalesOverTimeDTO;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderDetailRepository;
import com.example.ecommerce.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping
    public String showReportPage() {
        return "view/admin/reportManage/report";
    }

    @GetMapping("/sales-report")
    @ResponseBody
    public ResponseEntity<?> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        try {
            logger.info("Generating sales report from {} to {}", fromDate, toDate);

            // Validation ngày
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                logger.warn("Invalid date range: fromDate {} is after toDate {}", fromDate, toDate);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
                return ResponseEntity.badRequest().body(error);
            }

            if (toDate != null && toDate.isAfter(LocalDate.now())) {
                logger.warn("Invalid toDate: {} is in the future", toDate);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ngày kết thúc không thể lớn hơn ngày hiện tại");
                return ResponseEntity.badRequest().body(error);
            }

            // Chuyển đổi LocalDate thành LocalDateTime
            LocalDateTime fromDateTime = null;
            LocalDateTime toDateTime = null;
            
            if (fromDate != null) {
                fromDateTime = fromDate.atStartOfDay(); // 00:00:00
                logger.debug("Converted fromDate {} to fromDateTime {}", fromDate, fromDateTime);
            }
            
            if (toDate != null) {
                toDateTime = toDate.atTime(LocalTime.MAX); // 23:59:59.999999999
                logger.debug("Converted toDate {} to toDateTime {}", toDate, toDateTime);
            }

            // 1. Lọc đơn hàng đã hoàn tất
            List<Order> orders = orderRepository.findCompletedOrdersBetweenDates(fromDateTime, toDateTime);
            logger.info("Found {} completed orders in the specified date range", orders.size());

            // Kiểm tra nếu không có đơn hàng nào
            if (orders.isEmpty()) {
                logger.info("No orders found in the specified date range");
                Map<String, Object> result = new HashMap<>();
                result.put("totalRevenue", BigDecimal.ZERO);
                result.put("totalCustomers", 0L);
                result.put("totalProductsSold", 0);
                result.put("salesOverTime", List.of());
                result.put("salesByCategory", Map.of());
                result.put("customerDetails", List.of());
                result.put("productDetails", List.of());
                return ResponseEntity.ok(result);
            }

            BigDecimal totalRevenue = orders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalCustomers = orders.stream()
                    .map(order -> order.getUser().getUserId())
                    .distinct()
                    .count();

            List<Integer> orderIds = orders.stream()
                    .map(Order::getOrderId)
                    .toList();

            Integer totalProductsSold = orderDetailRepository.sumQuantityByOrderIds(orderIds);
            if (totalProductsSold == null) {
                totalProductsSold = 0;
            }

            // 2. Doanh thu theo thời gian
            List<SalesOverTimeDTO> salesOverTime = orderRepository.getRevenueGroupedByMonth(fromDateTime, toDateTime);

            // 3. Sản phẩm bán theo danh mục
            Map<String, Integer> salesByCategory = orderDetailRepository.getProductSoldByCategory(orderIds);

            // 4. Thông tin khách hàng
            List<CustomerReportDTO> customerDetails = orderRepository.getCustomerReport(fromDateTime, toDateTime);

            // 5. Thông tin sản phẩm
            List<ProductReportDTO> productDetails = orderDetailRepository.getProductSalesReport(orderIds);

            Map<String, Object> result = new HashMap<>();
            result.put("totalRevenue", totalRevenue);
            result.put("totalCustomers", totalCustomers);
            result.put("totalProductsSold", totalProductsSold);
            result.put("salesOverTime", salesOverTime);
            result.put("salesByCategory", salesByCategory);
            result.put("customerDetails", customerDetails);
            result.put("productDetails", productDetails);

            logger.info("Successfully generated sales report. Total revenue: {}, Total customers: {}, Total products sold: {}", 
                       totalRevenue, totalCustomers, totalProductsSold);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error generating sales report", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Có lỗi xảy ra khi xử lý dữ liệu: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
