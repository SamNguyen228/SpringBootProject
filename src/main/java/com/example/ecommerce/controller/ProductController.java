package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.OrderDetailService;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.ReviewService;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.model.viewmodel.CategoriesVM;
import com.example.ecommerce.model.viewmodel.ProductsDetailVM;
import com.example.ecommerce.model.viewmodel.ProductsVM;
import com.example.ecommerce.model.viewmodel.ReviewVM;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final OrderDetailService orderDetailService;
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService,
                             ReviewService reviewService,
                             UserService userService,
                             OrderDetailService orderDetailService,
                             CategoryService categoryService,
                             ProductRepository productRepository) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.orderDetailService = orderDetailService;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public String index(
            @RequestParam(required = false) Integer loai,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "popular") String sortBy,
            Model model) {

        page = page - 1;

        Sort sort;
        switch (sortBy) {
            case "price_asc":
                sort = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "price_desc":
                sort = Sort.by(Sort.Direction.DESC, "price");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "productId");
                break;
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Product> productPage;
        if (loai != null) {
            productPage = productRepository.findByCategoryId(loai, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<CategoriesVM> categories = categoryService.getAllCategories();

        List<ProductsVM> productVMs = productPage.getContent().stream()
                .map(p -> new ProductsVM(
                        p.getCategory().getCategoryId(),
                        p.getProductId(),
                        p.getProductName(),
                        p.getImageUrl(),
                        p.getPrice(),
                        p.getDescription(),
                        p.getCategory().getCategoryName()
                ))
                .collect(Collectors.toList());

        model.addAttribute("categories", categories);
        model.addAttribute("products", productVMs);
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("loai", loai);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("pageSize", pageSize);

        return "view/product";
    }

    @GetMapping("/product/search")
        public String search(
                @RequestParam(required = false) String query,
                @RequestParam(required = false) Integer loai,
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "9") int pageSize,
                @RequestParam(defaultValue = "popular") String sortBy,
                Model model) {

            page = page - 1;

            Sort sort;
            switch (sortBy) {
                case "price_asc":
                    sort = Sort.by(Sort.Direction.ASC, "price");
                    break;
                case "price_desc":
                    sort = Sort.by(Sort.Direction.DESC, "price");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.ASC, "productId");
                    break;
            }

            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Page<Product> productPage;

            if (query != null && !query.isEmpty()) {
                if (loai != null) {
                    productPage = productRepository.findByProductNameContainingIgnoreCaseAndCategoryId(query, loai, pageable);
                } else {
                    productPage = productRepository.findByProductNameContainingIgnoreCase(query, pageable);
                }
            } else {
                if (loai != null) {
                    productPage = productRepository.findByCategoryId(loai, pageable);
                } else {
                    productPage = productRepository.findAll(pageable);
                }
            }

            List<ProductsVM> productVMs = productPage.getContent().stream()
                    .map(p -> new ProductsVM(
                            p.getCategory().getCategoryId(),
                            p.getProductId(),
                            p.getProductName(),
                            p.getImageUrl(),
                            p.getPrice(),
                            p.getDescription(),
                            p.getCategory().getCategoryName()
                    )).collect(Collectors.toList());

            List<CategoriesVM> categories = categoryService.getAllCategories();

            model.addAttribute("categories", categories);
            model.addAttribute("products", productVMs);
            model.addAttribute("currentPage", page + 1);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("loai", loai);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("query", query);

            return "view/product";
        }

    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable("id") int id, Model model, Principal principal) {
        Product product = productService.getProductById(id);

        if (product == null) {
            model.addAttribute("message", "Không tìm thấy sản phẩm có mã " + id);
            return "redirect:/404";
        }

        List<Product> relatedProducts = productService.getRelatedProducts(id, product.getCategory().getCategoryId());
        List<ReviewVM> reviews = reviewService.getReviewsByProductId(id);

        double averageRating = 0;
        int[] starCounts = new int[5];
        if (!reviews.isEmpty()) {
            averageRating = reviews.stream().mapToInt(ReviewVM::getDanhGia).average().orElse(0);
            for (ReviewVM r : reviews) {
                if (r.getDanhGia() >= 1 && r.getDanhGia() <= 5) {
                    starCounts[r.getDanhGia() - 1]++;
                }
            }
        }

        boolean daMuaHang = false;
        boolean choPhepDanhGia = false;

        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);

            if (user != null) {
                int soLanMua = orderDetailService.countPurchasedProductByUser(id, user.getUserId());
                int soLanDanhGia = reviewService.countReviewsByUser(id, user.getUserId());

                if (soLanMua > 0) {
                    daMuaHang = true;
                    if (soLanMua > soLanDanhGia) {
                        choPhepDanhGia = true;
                    }
                }
            }
        }

        ProductsDetailVM result = new ProductsDetailVM();
        result.setMaSP(product.getProductId());
        result.setTenSP(product.getProductName());
        result.setGia(product.getPrice());
        result.setMoTa(product.getDescription());
        result.setHinhAnh(product.getImageUrl());
        result.setTenLoai(product.getCategory().getCategoryName());
        result.setMaLoai(product.getCategory().getCategoryId());
        result.setChiTiet(product.getDescription());
        result.setDiemDanhGia(averageRating);
        result.setSoLuongTon(product.getStockQuantity());
        result.setMauSac(product.getColor());
        result.setDungLuong(product.getCapacity());
        result.setSoLuong(product.getStockQuantity());
        result.setProducts(relatedProducts);
        result.setReviews(reviews);
        result.setDaMuaHang(daMuaHang);
        result.setChoPhepDanhGia(choPhepDanhGia);

        List<String> capacities = new ArrayList<>();
        if (product.getCapacity() != null && !product.getCapacity().isBlank()) {
            capacities = Arrays.stream(product.getCapacity().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        List<String> colors = new ArrayList<>();
        if (product.getColor() != null && !product.getColor().isBlank()) {
            colors = Arrays.stream(product.getColor().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        model.addAttribute("colors", colors);
        model.addAttribute("capacities", capacities);

        // Truyền vào model
        model.addAttribute("productDetail", result);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", reviews.size());
        model.addAttribute("starCounts", starCounts);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("reviews", reviews);
        System.out.println("starCounts: " + Arrays.toString(starCounts));
        System.out.println("reviews size: " + reviews.size());
        return "view/product-detail";
    }
}
