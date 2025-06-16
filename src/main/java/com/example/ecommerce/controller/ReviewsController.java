package com.example.ecommerce.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Review;
import com.example.ecommerce.model.ReviewRequest;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderDetailRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ReviewRepository;
import com.example.ecommerce.repository.UserRepository;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest model) {
        if (model == null || model.getDanhGia() < 1 || model.getDanhGia() > 5) {
            return ResponseEntity.badRequest().body("Dữ liệu đánh giá không hợp lệ.");
        }

        User user = userRepository.findByEmail(model.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại.");
        }

        // Kiểm tra đơn hàng đã hoàn tất
        boolean hasCompletedOrder = orderRepository
            .findByUser_UserIdAndStatus(user.getUserId(), "Completed")
            .stream()
            .flatMap(order -> orderDetailRepository.findByOrder_OrderId(order.getOrderId()).stream())
            .anyMatch(od -> od.getProduct().getProductId() == model.getMaSanPham());

        if (!hasCompletedOrder) {
            return ResponseEntity.badRequest().body("Bạn chưa trải nghiệm sản phẩm nên không thể đánh giá.");
        }

        Optional<Product> productOpt = productRepository.findById(model.getMaSanPham());
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sản phẩm không tồn tại.");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(productOpt.get());
        review.setReviewText(model.getNoiDung());
        review.setRating(model.getDanhGia());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return ResponseEntity.ok(Collections.singletonMap("message", "Đánh giá đã được thêm thành công!"));
    }
}
