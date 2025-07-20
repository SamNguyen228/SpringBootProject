package com.example.ecommerce.service;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderDetail;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderDetailRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public void sendOrderConfirmationEmail(String userEmailForm, String fullNameForm, String phoneForm, String addressForm, Order order) {
        User user = order.getUser();
        if (user == null && !StringUtils.hasText(userEmailForm)) {
            System.out.println("Không có thông tin người dùng hoặc email để gửi.");
            return;
        }

        // Ưu tiên thông tin nhập từ form
        String userEmail = StringUtils.hasText(userEmailForm) ? userEmailForm : (user != null ? user.getEmail() : null);
        String fullName = StringUtils.hasText(fullNameForm) ? fullNameForm : (user != null ? user.getFullName() : "Khách hàng");
        String phone = StringUtils.hasText(phoneForm) ? phoneForm : (user != null ? user.getPhone() : "N/A");
        String address = StringUtils.hasText(addressForm) ? addressForm : (user != null ? user.getAddress() : "N/A");

        if (!StringUtils.hasText(userEmail)) {
            System.out.println("Không có địa chỉ email để gửi xác nhận.");
            return;
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithProduct(order.getOrderId());

        StringBuilder productDetails = new StringBuilder();
        for (OrderDetail item : orderDetails) {
            productDetails.append(String.format(
                "<tr><td style='border:1px solid #ddd;padding:8px;'>%s</td>" +
                "<td style='border:1px solid #ddd;padding:8px;text-align:center;'>%d</td>" +
                "<td style='border:1px solid #ddd;padding:8px;text-align:right;'>%,.0f $</td>" +
                "<td style='border:1px solid #ddd;padding:8px;text-align:right;'>%,.0f $</td></tr>",
                item.getProduct().getProductName(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice() * item.getQuantity()
            ));
        }

        String subject = "Xác nhận đơn hàng - SmartBuy Store";
        String body = String.format(
            "<h2 style='color:#2d89ef;'>Cảm ơn bạn đã đặt hàng tại SmartBuy Store!</h2>" +
            "<p><strong>Mã đơn hàng:</strong> #%d</p>" +
            "<p><strong>Ngày đặt hàng:</strong> %s</p>" +
            "<p><strong>Tổng tiền:</strong> %,.0f $</p>" +
            "<p><strong>Giảm giá:</strong> %,.0f $</p>" +
            "<h3>Thông tin khách hàng:</h3>" +
            "<p><strong>Họ và tên:</strong> %s</p>" +
            "<p><strong>Điện thoại:</strong> %s</p>" +
            "<p><strong>Địa chỉ:</strong> %s</p>" +
            "<h3>Chi tiết đơn hàng:</h3>" +
            "<table style='border-collapse:collapse;width:100%%;font-size:16px;'>" +
            "<thead><tr style='background:#f4f4f4;'>" +
            "<th style='border:1px solid #ddd;padding:8px;'>Tên sản phẩm</th>" +
            "<th style='border:1px solid #ddd;padding:8px;'>Số lượng</th>" +
            "<th style='border:1px solid #ddd;padding:8px;'>Đơn giá</th>" +
            "<th style='border:1px solid #ddd;padding:8px;'>Thành tiền</th>" +
            "</tr></thead><tbody>%s</tbody></table>",
            order.getOrderId(),
            order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            order.getTotalAmount().doubleValue(),
            order.getDiscountAmount().doubleValue(),
            fullName, phone, address,
            productDetails.toString()
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            helper.setFrom("duonghoangsamet@gmail.com", "SmartBuy Store");

            mailSender.send(message);
            System.out.println("Đã gửi email xác nhận đơn hàng tới: " + userEmail);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            System.err.println("Lỗi khi gửi email xác nhận đơn hàng:");
            e.printStackTrace();
        }
    }
}
