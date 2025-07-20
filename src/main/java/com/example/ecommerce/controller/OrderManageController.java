package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderManageController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String listOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "filterStatus", required = false) String filterStatus,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> ordersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            ordersPage = orderRepository.searchByOrderIdLike(keyword, pageable);
        } else if (filterStatus != null && !filterStatus.trim().isEmpty()) {
            ordersPage = orderRepository.findByStatus(filterStatus, pageable);
        } else {
            ordersPage = orderRepository.findAll(pageable);
        }

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("filterStatus", filterStatus);
        model.addAttribute("pageTitle", "Quản lí đơn hàng");

        return "view/admin/orderManage/order-list";
    }

    @GetMapping("/{id}")
    public String viewAndEditOrder(@PathVariable("id") Integer id, Model model) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            model.addAttribute("pageTitle", "Chi tiết đơn hàng");
            return "view/admin/orderManage/order-manage-detail";
        } else {
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/{id}/next")
    public String moveToNextStatus(@PathVariable("id") Integer id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            String current = order.getStatus();

            switch (current) {
                case "Pending" -> order.setStatus("Processing");
                case "Processing" -> order.setStatus("Shipped");
                case "Shipped" -> order.setStatus("Completed");
            }

            orderRepository.save(order);
        }

        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Integer id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (!"Completed".equals(order.getStatus())) {
                order.setStatus("Cancelled");
                orderRepository.save(order);
            }
        }

        return "redirect:/admin/orders/" + id;
    }
}
