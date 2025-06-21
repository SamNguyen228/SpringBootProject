package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/order-history")
    public String orderHistory(Model model) {
        Integer userId = userService.getUserId();
        if (userId == null || userId == 0) {
            return "redirect:/404";
        }

        List<Order> orders = orderRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        model.addAttribute("orders", orders);
        return "view/user/order-history";
    }

    @GetMapping("/order-detail/{orderId}")
    public String orderDetails(@PathVariable("orderId") Integer orderId, Model model) {
        Integer userId = userService.getUserId();
        if (userId == null || userId == 0) {
            return "redirect:/login";
        }

        Optional<Order> orderOpt = orderRepository.findByOrderIdAndUser_UserId(orderId, userId);
        if (orderOpt.isEmpty()) {
            return "redirect:/404";
        }

        model.addAttribute("order", orderOpt.get());
        return "view/user/order-detail";
    }


    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam("orderId") int orderId, RedirectAttributes redirectAttributes, Principal principal) {
        String Email = principal.getName();
        User user = userService.findByEmail(Email);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            redirectAttributes.addFlashAttribute("cancelError", "Đơn hàng không tồn tại!");
            return "redirect:/orders";
        }

        Order order = optionalOrder.get();

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            redirectAttributes.addFlashAttribute("cancelError", "Bạn không có quyền hủy đơn hàng này!");
            return "redirect:/orders";
        }

        if (!"Pending".equals(order.getStatus())) {
            redirectAttributes.addFlashAttribute("cancelError", "Không thể hủy đơn hàng này. Đơn hàng đã được xử lý.");
            return "redirect:/orders";
        }

        order.setStatus("Cancelled");
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("cancelSuccess", "Đơn hàng đã được hủy thành công!");
        return "redirect:/order-detail/" + orderId;
    }
}

