package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomerReportDTO;
import com.example.ecommerce.dto.ProductReportDTO;
import com.example.ecommerce.dto.SalesOverTimeDTO;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderDetailRepository;
import com.example.ecommerce.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

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

        // 1. Lọc đơn hàng đã hoàn tất
        List<Order> orders = orderRepository.findCompletedOrdersBetweenDates(fromDate, toDate);

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

        // 2. Doanh thu theo thời gian
        List<SalesOverTimeDTO> salesOverTime = orderRepository.getRevenueGroupedByMonth(fromDate, toDate);

        // 3. Sản phẩm bán theo danh mục
        Map<String, Integer> salesByCategory = orderDetailRepository.getProductSoldByCategory(orderIds);

        // 4. Thông tin khách hàng
        List<CustomerReportDTO> customerDetails = orderRepository.getCustomerReport(fromDate, toDate);

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

        return ResponseEntity.ok(result);
    }
}
