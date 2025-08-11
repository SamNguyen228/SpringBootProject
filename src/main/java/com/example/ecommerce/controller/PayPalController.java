package com.example.ecommerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.PayPalService;

@Controller
@RequestMapping("/paypal")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Tạo thanh toán PayPal với QR code
     */
    @PostMapping("/create-payment")
    @ResponseBody
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestParam Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Đơn hàng không tồn tại"));
            }

            Map<String, Object> paymentResult = payPalService.createQRPayment(order);
            return ResponseEntity.ok(paymentResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Lỗi tạo thanh toán: " + e.getMessage()));
        }
    }

    /**
     * Xác minh thanh toán PayPal
     */
    @PostMapping("/verify-payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestParam String paymentId,
            @RequestParam String payerId) {
        try {
            Map<String, Object> verificationResult = payPalService.verifyPayment(paymentId, payerId);
            return ResponseEntity.ok(verificationResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Lỗi xác minh thanh toán: " + e.getMessage()));
        }
    }

    /**
     * Trang thanh toán PayPal
     */
    @GetMapping("/payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String paymentPage(@RequestParam Integer orderId, org.springframework.ui.Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/user/cart";
        }

        model.addAttribute("order", order);
        model.addAttribute("paypalConfig", payPalService.getPayPalConfig());
        return "view/user/paypal-payment";
    }

    /**
     * Trang thành công sau khi thanh toán
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentId, 
                               @RequestParam String payerId,
                               org.springframework.ui.Model model) {
        try {
            Map<String, Object> verificationResult = payPalService.verifyPayment(paymentId, payerId);
            model.addAttribute("verificationResult", verificationResult);
            model.addAttribute("paymentId", paymentId);
            model.addAttribute("payerId", payerId);
            
            return "view/user/payment-success";
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xác minh thanh toán: " + e.getMessage());
            return "view/user/payment-error";
        }
    }

    /**
     * Trang hủy thanh toán
     */
    @GetMapping("/cancel")
    public String paymentCancel(org.springframework.ui.Model model) {
        model.addAttribute("message", "Bạn đã hủy thanh toán PayPal");
        return "view/user/payment-cancel";
    }

    /**
     * Lấy cấu hình PayPal cho frontend
     */
    @GetMapping("/config")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> config = payPalService.getPayPalConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "error", "Lỗi lấy cấu hình: " + e.getMessage()));
        }
    }
}
