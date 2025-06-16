/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.ecommerce.repository;

import com.example.ecommerce.model.Category;
import com.example.ecommerce.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(Integer categoryId);
    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);

    public Optional<Product> findById(Integer id);

    public void deleteById(Integer id);

    List<Product> findTop4ByCategoryAndProductIdNot(Category category, int productId);

    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategoryId(String name, Integer categoryId, Pageable pageable);

}
