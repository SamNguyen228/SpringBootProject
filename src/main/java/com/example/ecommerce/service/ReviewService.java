package com.example.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.model.viewmodel.ReviewVM;
import com.example.ecommerce.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewVM> getReviewsByProductId(int productId) {
        return reviewRepository.findByProduct_ProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(rv -> {
                    ReviewVM vm = new ReviewVM();
                    vm.setMaReview(rv.getReviewId());
                    vm.setMaKhachHang(rv.getUser() != null ? rv.getUser().getUserId() : null);
                    vm.setTenKhachHang(rv.getUser() != null ? rv.getUser().getFullName() : "Ẩn danh");
                    vm.setMaSanPham(rv.getProduct().getProductId());
                    vm.setDanhGia(rv.getRating());
                    vm.setNoiDung(rv.getReviewText());
                    vm.setNgayDang(rv.getCreatedAt());
                    return vm;
                })
                .collect(Collectors.toList());
    }

    public int countReviewsByUser(int productId, int userId) {
        return (int) reviewRepository
                . findByProduct_ProductIdAndUser_UserId(productId, userId)
                .stream()
                .count();
    }
}
