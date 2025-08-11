package com.example.ecommerce.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.ecommerce.model.Order;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Value("${paypal.return.url}")
    private String returnUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    /**
     * Tạo QR code cho thanh toán PayPal
     */
    public Map<String, Object> createQRPayment(Order order) {
        try {
            // Tạo unique payment ID
            String paymentId = UUID.randomUUID().toString();
            
            // Tạo PayPal checkout URL với QR
            String paypalUrl = generatePayPalCheckoutURL(order, paymentId);
            
            // Tạo QR code data
            String qrData = generateQRData(order, paymentId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("paymentId", paymentId);
            result.put("paypalUrl", paypalUrl);
            result.put("qrData", qrData);
            result.put("amount", order.getTotalAmount());
            result.put("currency", "USD");
            result.put("orderId", order.getOrderId());
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Không thể tạo thanh toán PayPal: " + e.getMessage());
            return result;
        }
    }

    /**
     * Tạo PayPal checkout URL
     */
    private String generatePayPalCheckoutURL(Order order, String paymentId) {
        String baseUrl = "sandbox".equals(mode) 
            ? "https://www.sandbox.paypal.com/checkoutnow" 
            : "https://www.paypal.com/checkoutnow";
        
        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?token=").append(paymentId);
        url.append("&amount=").append(order.getTotalAmount());
        url.append("&currency=USD");
        url.append("&return=").append(returnUrl);
        url.append("&cancel=").append(cancelUrl);
        url.append("&item_name=Order_").append(order.getOrderId());
        
        return url.toString();
    }

    /**
     * Tạo dữ liệu cho QR code
     */
    private String generateQRData(Order order, String paymentId) {
        // Tạo dữ liệu QR theo format PayPal
        StringBuilder qrData = new StringBuilder();
        qrData.append("paypal://");
        qrData.append("payment?");
        qrData.append("id=").append(paymentId);
        qrData.append("&amount=").append(order.getTotalAmount());
        qrData.append("&currency=USD");
        qrData.append("&item_name=Order_").append(order.getOrderId());
        
        return qrData.toString();
    }

    /**
     * Xác minh thanh toán PayPal
     */
    public Map<String, Object> verifyPayment(String paymentId, String payerId) {
        try {
            // Trong thực tế, bạn sẽ gọi PayPal API để xác minh
            // Ở đây tôi chỉ mô phỏng
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("verified", true);
            result.put("paymentId", paymentId);
            result.put("payerId", payerId);
            result.put("message", "Thanh toán đã được xác minh thành công");
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Không thể xác minh thanh toán: " + e.getMessage());
            return result;
        }
    }

    /**
     * Lấy thông tin cấu hình PayPal
     */
    public Map<String, Object> getPayPalConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("clientId", clientId);
        config.put("mode", mode);
        config.put("returnUrl", returnUrl);
        config.put("cancelUrl", cancelUrl);
        return config;
    }
}
