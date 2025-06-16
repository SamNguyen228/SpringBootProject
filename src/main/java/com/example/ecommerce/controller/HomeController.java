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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
            products = productRepository.findByCategoryId(loai);
        } else {
            products = productRepository.findAll();
        }

        List<ProductsVM> result = products.stream().map(p -> {
            ProductsVM vm = new ProductsVM();
            vm.setMaLoai(p.getCategory().getCategoryId());
            vm.setMaSP(p.getProductId());
            vm.setTenSP(p.getProductName());
            vm.setHinhAnh(p.getImageUrl());
            vm.setGia(p.getPrice());
            vm.setMoTa(p.getDescription());
            vm.setTenLoai(p.getCategory().getCategoryName());
            return vm;
        }).collect(Collectors.toList());

        List<ProductsVM> iphoneTop3 = result.stream()
        .filter(p -> "iPhone".equalsIgnoreCase(p.getTenLoai()))
        .limit(3)
        .collect(Collectors.toList());

        List<ProductsVM> ipadTop3 = result.stream()
        .filter(p -> "iPad".equalsIgnoreCase(p.getTenLoai()))
        .limit(3)
        .collect(Collectors.toList());

        List<ProductsVM> macbookTop3 = result.stream()
        .filter(p -> "MacBook".equalsIgnoreCase(p.getTenLoai()))
        .limit(3)
        .collect(Collectors.toList());

        List<ProductsVM> airpodsTop3 = result.stream()
        .filter(p -> "AirPods".equalsIgnoreCase(p.getTenLoai()))
        .limit(3)
        .collect(Collectors.toList());

        Random rand = new Random();
        List<Map<String, Object>> appleProducts = result.stream()
                .filter(p -> p.getTenLoai().equalsIgnoreCase("iPhone")
                        || p.getTenLoai().equalsIgnoreCase("Apple Watch")
                        || p.getTenLoai().equalsIgnoreCase("MacBook"))
                .map(p -> {
                    int discount = rand.nextInt(41) + 10; 
                    double oldPrice = p.getGia() * (1 + discount / 100.0);
                    Map<String, Object> map = new HashMap<>();
                    map.put("product", p);
                    map.put("discount", discount);
                    map.put("oldPrice", oldPrice);
                    return map;
                }).collect(Collectors.toList());

        model.addAttribute("products", result);     
        model.addAttribute("appleProducts", appleProducts);
        model.addAttribute("iphoneTop3", iphoneTop3);
        model.addAttribute("ipadTop3", ipadTop3);
        model.addAttribute("macbookTop3", macbookTop3);
        model.addAttribute("airpodsTop3", airpodsTop3);

        return "index";
    }

    @GetMapping("/404")
    public String pageNotFound() {
        return "view/pagenotfound";
    }

    @GetMapping("/hotdeal")
    public String hotDeal(Model model) {
        List<Product> products = productRepository.findAll();
        // Map products to a list of maps containing ProductsVM, discount, and oldPrice
        List<Map<String, Object>> result = products.stream().map(p -> {
            ProductsVM vm = new ProductsVM();
            vm.setMaLoai(p.getCategory().getCategoryId());
            vm.setMaSP(p.getProductId());
            vm.setTenSP(p.getProductName());
            vm.setHinhAnh(p.getImageUrl());
            vm.setGia(p.getPrice());
            vm.setMoTa(p.getDescription());
            vm.setTenLoai(p.getCategory().getCategoryName());
            // Calculate random discount (10% to 50%) and old price
            Random random = new Random();
            int discount = random.nextInt(41) + 10;
            double oldPrice = p.getPrice() * (1 + discount / 100.0);
            // Create a map for the product
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("product", vm);
            productMap.put("discount", discount);
            productMap.put("oldPrice", oldPrice);
            return productMap;
        }).collect(Collectors.toList());
        
        // Group by category and limit to 4 products per category
        Map<String, List<Map<String, Object>>> categoryMap = result.stream()
                .collect(Collectors.groupingBy(
                        map -> ((ProductsVM) map.get("product")).getTenLoai(),
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream().limit(4).collect(Collectors.toList()))));
            
        model.addAttribute("categoryMap", categoryMap);
        return "view/hotdeal";
    }
    
    @GetMapping("/contact")
    public String contact(ChatMessage chatMessage) {
        return "view/contact";
    }

    @PostMapping("/submitForm")
    public String submitForm(@Valid ChatMessage chatMessage, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "view/contact";
        }
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        model.addAttribute("successMessage", "Tin nhắn đã được gửi thành công!");
        return "redirect:/contact";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; 
    }
}

