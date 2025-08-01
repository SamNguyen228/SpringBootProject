package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Notification;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderDetail;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.Promotion;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.viewmodel.CartItem;
import com.example.ecommerce.model.viewmodel.CheckoutViewModel;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.NotificationRepository;
import com.example.ecommerce.repository.OrderDetailRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.PromotionRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.ShippingAddressRepository;
import com.example.ecommerce.service.EmailService;
import com.example.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('CUSTOMER')")
public class CheckoutController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/checkout")
    public String index(Model model, Principal principal, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = userService.getUserId();

        List<CartItem> cartItems = cartRepository.findByUser_UserId(userId).stream().map(c -> {
            CartItem item = new CartItem();
            item.setMaSP(c.getProduct().getProductId());
            item.setTenSP(c.getProduct().getProductName());
            item.setGia(c.getProduct().getPrice());
            item.setHinhAnh(c.getProduct().getImageUrl());
            item.setSoLuong(c.getQuantity());
            return item;
        }).toList();

        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("cartEmpty", "Giỏ hàng của bạn đang trống.");
            return "redirect:/user/cart";
        }

        // Kiểm tra tồn kho
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getMaSP()).orElse(null);
            if (product == null) {
                redirectAttributes.addFlashAttribute("cartError", "Sản phẩm không tồn tại: " + item.getTenSP());
                return "redirect:/user/cart";
            }

            if (item.getSoLuong() > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("cartError",
                        "Sản phẩm " + item.getTenSP() + " chỉ còn " + product.getStockQuantity() + " sản phẩm trong kho.");
                return "redirect:/user/cart";
            }
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getGia() * item.getSoLuong())
                .sum();

        User user = userRepository.findById(userId.intValue()).orElse(null);
        var shippingAddress = shippingAddressRepository.findFirstByUserId(userId);

        CheckoutViewModel checkoutModel = new CheckoutViewModel();
        checkoutModel.setFullName(user != null ? user.getFullName() : "Chưa cập nhật");
        checkoutModel.setEmail(user != null ? user.getEmail() : "Chưa cập nhật");
        checkoutModel.setPhone(user != null ? user.getPhone() : "Chưa cập nhật");
        checkoutModel.setAddress(shippingAddress != null ? shippingAddress.getAddress() : "");
        checkoutModel.setCartItems(cartItems);
        checkoutModel.setTotalAmount(totalAmount);

        model.addAttribute("checkoutModel", checkoutModel);
        return "view/user/checkout";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/process")
    public String processOrder(@RequestParam(required = false) Integer promotionId,
            @ModelAttribute("user") User userForm,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer userId = userService.getCurrentUserId();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đặt hàng.");
            return "redirect:/login";
        }

        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại.");
            return "redirect:/cart";
        }

        User user = userOpt.get();
        String fullName = Optional.ofNullable(userForm.getFullName()).orElse(user.getFullName());
        String email = Optional.ofNullable(userForm.getEmail()).orElse(user.getEmail());
        String phone = Optional.ofNullable(userForm.getPhone()).orElse(user.getPhone());
        String address = Optional.ofNullable(userForm.getAddress()).orElse(user.getAddress());

        List<Cart> carts = cartRepository.findByUserIdWithProduct(userId);
        if (carts.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống hoặc không hợp lệ.");
            return "redirect:/cart";
        }

        Map<Integer, Product> productMap = carts.stream()
                .map(Cart::getProduct)
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProduct().getProductId());
            if (product.getStockQuantity() < cart.getQuantity()) {
                redirectAttributes.addFlashAttribute("error",
                        "Sản phẩm " + product.getProductName() + " không đủ hàng.");
                return "redirect:/cart";
            }
        }

        BigDecimal totalAmount = carts.stream()
                .map(c -> BigDecimal.valueOf(c.getProduct().getPrice().doubleValue())
                        .multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = null;

        if (promotionId != null) {
            promotion = promotionRepository.findValidPromotion(promotionId, LocalDateTime.now());
            if (promotion != null &&
                    (promotion.getMinOrderValue() == null
                            || totalAmount.compareTo(promotion.getMinOrderValue()) >= 0)) {

                BigDecimal discountByAmount = promotion.getDiscountAmount() != null ? promotion.getDiscountAmount()
                        : BigDecimal.ZERO;
                BigDecimal discountByPercentage = promotion.getDiscountPercentage() != null
                        ? totalAmount.multiply(promotion.getDiscountPercentage().divide(BigDecimal.valueOf(100)))
                        : BigDecimal.ZERO;

                discountAmount = promotion.getDiscountAmount() != null ? discountByAmount : discountByPercentage;
                if (promotion.getMaxDiscount() != null) {
                    discountAmount = discountAmount.min(promotion.getMaxDiscount());
                }
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(finalAmount);
        order.setDiscountAmount(discountAmount);
        order.setStatus("Pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setPromotion(promotion);
        orderRepository.save(order);

        for (Cart cart : carts) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(cart.getProduct());
            detail.setQuantity(cart.getQuantity());
            detail.setPrice(cart.getProduct().getPrice());
            orderDetailRepository.save(detail);

            Product product = cart.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cart.getQuantity());
            productRepository.save(product);
        }

        cartRepository.deleteAll(carts);

        emailService.sendOrderConfirmationEmail(email, fullName, phone, address, order);

        Notification notification = new Notification();
        notification.setContent("Đơn hàng #" + order.getOrderId() + " đã được đặt. Tổng: " + finalAmount + "$");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);

        redirectAttributes.addFlashAttribute("orderSuccess", "Đặt hàng thành công! Đơn hàng đã được tạo!");
        return "redirect:/user/orders/confirmation/" + order.getOrderId();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/orders/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable int orderId, Model model) {
        Optional<Order> orderOpt = orderRepository.findByIdWithDetails(orderId);
        if (orderOpt.isEmpty())
            return "redirect:/";

        model.addAttribute("order", orderOpt.get());
        return "view/user/confirmation";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/apply-promotion")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyPromotion(@RequestParam("code") String code, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (!StringUtils.hasText(code)) {
            response.put("success", false);
            response.put("message", "Mã giảm giá không hợp lệ.");
            return ResponseEntity.ok(response);
        }

        Promotion promotion = promotionRepository.findValidByCode(code, LocalDateTime.now());

        if (promotion == null) {
            response.put("success", false);
            response.put("message", "Mã giảm giá không tồn tại hoặc đã hết hạn.");
            return ResponseEntity.ok(response);
        }

        User user = userRepository.findByEmail(principal.getName());
        List<Cart> cartItems = cartRepository.findByUserIdWithProduct(user.getUserId());

        if (cartItems.isEmpty()) {
            response.put("success", false);
            response.put("message", "Giỏ hàng trống.");
            return ResponseEntity.ok(response);
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount;

        if (promotion.getDiscountAmount() != null) {
            discountAmount = promotion.getDiscountAmount();
        } else {
            discountAmount = totalAmount.multiply(
                    promotion.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
        }

        if (promotion.getMaxDiscount() != null) {
            discountAmount = discountAmount.min(promotion.getMaxDiscount());
        }

        BigDecimal finalTotal = totalAmount.subtract(discountAmount);

        response.put("success", true);
        response.put("promotionId", promotion.getPromotionId());
        response.put("discount", discountAmount);
        response.put("totalAmount", finalTotal);

        return ResponseEntity.ok(response);
    }
}
