package com.example.ecommerce.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.viewmodel.CartItem;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;
 
    @GetMapping("/cart")
    public String showCart(Model model) {
        Integer userId = userService.getUserId();
        List<CartItem> cartItems = cartRepository.findByUser_UserId(userId)
            .stream()
            .map(c -> {
                CartItem item = new CartItem();
                item.setMaSP(c.getProduct().getProductId());
                item.setTenSP(c.getProduct().getProductName());
                item.setGia(c.getProduct().getPrice().doubleValue());
                item.setHinhAnh(c.getProduct().getImageUrl());
                item.setSoLuong(c.getQuantity());
                return item;
            }).collect(Collectors.toList());
        
        double totalAmount = cartItems.stream()
            .mapToDouble(item -> item.getGia() * item.getSoLuong())
            .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("userId", userId);

        model.addAttribute("cartItems", cartItems);
        return "view/user/cart";
    }

    @PostMapping("/cart/add/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String addToCart(@PathVariable("id") int productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            @RequestParam(value = "redirect", required = false) String redirectTarget,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        Integer userId = userService.getUserId();
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("addError", "Sản phẩm không tồn tại");
            return "redirect:" + request.getHeader("Referer");
        }

        if ("cart".equals(redirectTarget)) {
            return "redirect:/user/cart";
        }

        Product product = productOpt.get();

        if (product.getStockQuantity() < quantity) {
            redirectAttributes.addFlashAttribute("addError", "Sản phẩm đã hết hàng hoặc số lượng không đủ!");
            return "redirect:" + request.getHeader("Referer");
        }

        Optional<Cart> cartOpt = cartRepository.findByUser_UserIdAndProductId(userId, productId);

        Cart cartItem;
        if (cartOpt.isEmpty()) {
            cartItem = new Cart(userId, productId, quantity);
        } else {
            cartItem = cartOpt.get();
            if (cartItem.getQuantity() + quantity > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("addError", "Số lượng sản phẩm trong giỏ hàng vượt quá tồn kho!");
                return "redirect:" + request.getHeader("Referer");
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartRepository.save(cartItem);
        redirectAttributes.addFlashAttribute("addSuccess", "Sản phẩm đã được thêm vào giỏ hàng!");

        if (redirectTarget != null && !redirectTarget.isBlank()) {
            return "redirect:" + redirectTarget;
        }

        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/cart/remove/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String removeCartItem(@PathVariable("id") int productId,
                                RedirectAttributes redirectAttributes) {
        Integer userId = userService.getUserId();
        Optional<Cart> cartItem = cartRepository.findByUser_UserIdAndProductId(userId, productId);

        cartItem.ifPresent(cartRepository::delete);

        redirectAttributes.addFlashAttribute("deleteSuccess", "Sản phẩm đã được xóa khỏi giỏ hàng!");
        return "redirect:/user/cart";
    }

    @PostMapping("/cart/update-quantity")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam int id,
                                            @RequestParam int quantity,
                                            Principal principal) {
        Integer userId = userService.getUserId();

        Optional<Cart> optionalCartItem = cartRepository.findByUser_UserIdAndProductId(userId, id);
        if (optionalCartItem.isPresent()) {
            Cart cartItem = optionalCartItem.get();
            cartItem.setQuantity(quantity);
            cartRepository.save(cartItem);
        }

        List<Cart> cartItems = cartRepository.findByUser_UserId(userId);
        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", String.format("%.2f", total));
        return response;
    }

    @GetMapping("/cart-quick")
    public String quickCart(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("cartCount", 0);
            model.addAttribute("cartTotal", 0.0);
            return "fragments/cart-quick"; 
        }

        Integer userId = Integer.parseInt(((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal().toString());

        List<Cart> cartItems = cartService.getCartItemsByUserId(userId);
        int cartCount = cartService.getCartTotalQuantity(cartItems);
        double cartTotal = cartService.getCartTotalAmount(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("cartTotal", cartTotal);

        return "fragments/cart-quick"; 
    }
}
