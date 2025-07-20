// ProductController.java
package com.example.ecommerce.controller;

import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class ProductManageController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("productId").ascending());
        Page<Product> productsPage;

        if (keyword != null && !keyword.isEmpty()) {
            productsPage = productRepository.searchByProductNameContainingIgnoreCase(keyword, pageable);
        } else if (categoryId != null) {
            productsPage = productRepository.findByCategory_CategoryId(categoryId, pageable);
        } else {
            productsPage = productRepository.findAll(pageable);
        }

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("pageTitle", "Quản lí sản phẩm");

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryRepository.findAll());

        return "view/admin/productManage/product-manage";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("pageTitle", "Tạo mới sản phẩm");
        return "view/admin/productManage/edit-create-product";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "view/admin/productManage/edit-create-product";
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product.get());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("pageTitle", "Cập nhật sản phẩm");
        return "view/admin/productManage/edit-create-product";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, @ModelAttribute Product product, BindingResult result) {
        if (result.hasErrors()) {
            return "view/admin/productManage/edit-create-product";
        }
        product.setProductId(id);
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}
