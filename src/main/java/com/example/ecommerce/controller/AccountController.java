/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "accounts/login";
    }

    // @PostMapping("/login")
    // public String loginPost(
    //         @RequestParam String email,
    //         @RequestParam String password,
    //         Model model,
    //         HttpSession session) {

    //     User user = userRepository.findByEmail(email);

    //     if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
    //         model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
    //         return "accounts/login";
    //     }

    //     if (user.isLocked()) {
    //         model.addAttribute("error", "Tài khoản của bạn đã bị khóa!");
    //         return "accounts/login";
    //     }

    //     // Lưu thông tin user vào session
    //     session.setAttribute("userId", user.getUserId());
    //     session.setAttribute("userName", user.getFullName() != null ? user.getFullName() : "User");

    //     String roleName;
    //     switch (user.getRoleId()) {
    //         case 1:
    //             roleName = "Admin";
    //             break;
    //         case 2:
    //             roleName = "Customer";
    //             break;
    //         default:
    //             roleName = "Customer";
    //     }
    //     session.setAttribute("role", roleName);

    //     // Có thể lưu thêm thời gian hết hạn hoặc xử lý đăng nhập nâng cao ở đây

    //     model.addAttribute("loginSuccess", "Đăng nhập thành công!");
    //     return "redirect:/index";
    // }
    
    @GetMapping("/register")
    public String showRegisterForm() {
        return "accounts/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String address,
            Model model
    ) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "accounts/register";
        }

        // Họ tên
        if (!StringUtils.hasText(fullName) || fullName.length() < 2) {
            model.addAttribute("error", "Họ tên không được để trống và phải từ 2 ký tự trở lên!");
            return "accounts/register";
        }

        // Địa chỉ
        if (!StringUtils.hasText(address)) {
            model.addAttribute("error", "Địa chỉ không được để trống!");
            return "accounts/register";
        }

        // Email hợp lệ
        String emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        if (!StringUtils.hasText(email) || !Pattern.matches(emailPattern, email)) {
            model.addAttribute("error", "Email không hợp lệ!");
            return "accounts/register";
        }

        // Mật khẩu: ít nhất 6 ký tự, gồm cả chữ và số
        if (!StringUtils.hasText(password) || password.length() < 6 ||
                !password.chars().anyMatch(Character::isLetter) ||
                !password.chars().anyMatch(Character::isDigit)) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự và bao gồm cả chữ và số!");
            return "accounts/register";
        }

        // Số điện thoại: 10 chữ số
        if (!StringUtils.hasText(phone) || phone.length() != 10 || !phone.chars().allMatch(Character::isDigit)) {
            model.addAttribute("error", "Số điện thoại phải gồm đúng 10 chữ số!");
            return "accounts/register";
        }

        // Mã hóa mật khẩu
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Tạo người dùng mới
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIsLocked(false);
        newUser.setRoleId(2); // gán vai trò mặc định

        userRepository.save(newUser);
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // hủy phiên đăng nhập
        }
        request.setAttribute("logoutSuccess", "Đăng xuất thành công!");
        return "redirect:/";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}

