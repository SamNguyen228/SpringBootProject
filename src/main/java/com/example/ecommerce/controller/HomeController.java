/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ecommerce.controller;

import com.example.ecommerce.model.ChatMessage;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.viewmodel.ProductsVM;
import com.example.ecommerce.repository.ChatMessageRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping({"/", "/index"})
    public String index(@RequestParam(required = false) Integer loai, Model model) {
        List<Product> products;

        if (loai != null) {
            products = productRepository.findByCategory_CategoryId(loai);
        } else {
            products = productRepository.findAll();
        }

        List<ProductsVM> result = products.stream().map(p -> new ProductsVM(
            p.getCategory().getCategoryId(),
            p.getProductId(),
            p.getProductName(),
            p.getImageUrl(),
            p.getPrice(),
            p.getDescription(),
            p.getCategory().getCategoryName()
        )).collect(Collectors.toList());

        model.addAttribute("products", result);
        return "index"; // thymeleaf template index.html
    }

    @GetMapping("/404")
    public String pageNotFound() {
        return "404"; // template 404.html
    }

    @GetMapping("/hotdeal")
    public String hotDeal(Model model) {
        List<Product> products = productRepository.findAll();
        List<ProductsVM> result = (List<ProductsVM>) products.stream().map(p -> new ProductsVM(
            p.getCategory().getCategoryId(),
            p.getProductId(),
            p.getProductName(),
            p.getImageUrl(),
            p.getPrice(),
            p.getDescription(),
            p.getCategory().getCategoryName()
        )).collect(Collectors.toList());

        model.addAttribute("products", result);
        return "hotdeal"; // hotdeal.html
    }

    @GetMapping("/contact")
    public String contact(ChatMessage chatMessage) {
        return "contact"; // contact.html
    }

    @PostMapping("/submitForm")
    public String submitForm(@Valid ChatMessage chatMessage, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Nếu lỗi validate, trả về form
            return "contact";
        }
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        model.addAttribute("successMessage", "Tin nhắn đã được gửi thành công!");
        return "redirect:/contact";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // dashboard.html
    }
}

