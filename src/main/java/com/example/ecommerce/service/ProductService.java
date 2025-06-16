package com.example.ecommerce.service;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Integer id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    public Product findById(Integer id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getRelatedProducts(int productId, int categoryId) {   
        // Lấy sản phẩm đang xem
        Product currentProduct = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // Lấy 4 sản phẩm cùng danh mục (khác id hiện tại)
        return productRepository.findTop4ByCategoryAndProductIdNot(currentProduct.getCategory(), productId);
    }
}
