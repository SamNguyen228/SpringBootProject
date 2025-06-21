/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.model.viewmodel.ForgotPasswordModel;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    
    @Autowired 
    private JavaMailSender mailSender;

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/index"; 
        }
        return "accounts/login"; 
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        User user = userRepository.findByEmail(email);

        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
            return "accounts/login";
        }

        if (user.isLocked()) {
            model.addAttribute("error", "Tài khoản của bạn đã bị khóa!");
            return "accounts/login";
        }

        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getFullName() != null ? user.getFullName() : "User");

        String roleName = switch (user.getRoleId()) {
            case 1 -> "ADMIN";
            case 2 -> "CUSTOMER";
            default -> "GUEST";
        };
        session.setAttribute("role", roleName);

        session.setMaxInactiveInterval(60 * 60);

        if ("ADMIN".equals(roleName)) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/index";
        }
    }

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
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "accounts/register";
        }

        if (!StringUtils.hasText(fullName) || fullName.length() < 2) {
            model.addAttribute("error", "Họ tên không được để trống và phải từ 2 ký tự trở lên!");
            return "accounts/register";
        }

        if (!StringUtils.hasText(address)) {
            model.addAttribute("error", "Địa chỉ không được để trống!");
            return "accounts/register";
        }

        String emailPattern = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        if (!StringUtils.hasText(email) || !Pattern.matches(emailPattern, email)) {
            model.addAttribute("error", "Email không hợp lệ!");
            return "accounts/register";
        }

        if (!StringUtils.hasText(password) || password.length() < 6 ||
                !password.chars().anyMatch(Character::isLetter) ||
                !password.chars().anyMatch(Character::isDigit)) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự và bao gồm cả chữ và số!");
            return "accounts/register";
        }

        if (!StringUtils.hasText(phone) || phone.length() != 10 || !phone.chars().allMatch(Character::isDigit)) {
            model.addAttribute("error", "Số điện thoại phải gồm đúng 10 chữ số!");
            return "accounts/register";
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIsLocked(false);
        newUser.setRoleId(2);

        userRepository.save(newUser);
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.setAttribute("logoutSuccess", "Đăng xuất thành công!");
        return "redirect:/";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "view/access-denied";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        Integer userId = userService.getUserId();

        if (userId == null) {
            return "redirect:/login";
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "error/404"; 
        }

        model.addAttribute("user", userOptional.get());
        return "accounts/profile"; 
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
         Integer userId = userService.getUserId();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("user", userOpt.get());
        return "accounts/edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("user") User formUser,
                                @RequestParam(required = false) String newPassword,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

         Integer userId = userService.getUserId();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOpt.get();

        user.setFullName(formUser.getFullName());
        user.setEmail(formUser.getEmail());
        user.setPhone(formUser.getPhone());
        user.setAddress(formUser.getAddress());

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/";
    }

   @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordModel model, HttpServletRequest request) {
        User user = userRepository.findByEmail(model.getEmail());
        if (user == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Email không tồn tại, vui lòng nhập lại."));
        }

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + "/account/reset-password?token=" + token;

        sendResetEmail(user.getEmail(), resetLink);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null || user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "account/reset-password-expired";
        }

        model.addAttribute("token", token);
        return "account/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token,
                                    @RequestParam String newPassword,
                                    @RequestParam String confirmPassword,
                                    Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            model.addAttribute("token", token);
            return "account/reset-password";
        }

        User user = userRepository.findByResetPasswordToken(token);
        if (user == null || user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "account/reset-password-expired";
        }

        user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);
        userRepository.save(user);

        return "redirect:/login?resetSuccess";
    }

    private void sendResetEmail(String toEmail, String resetLink) {
        String subject = "Đặt lại mật khẩu";
        String body = "<p>Bạn vừa yêu cầu đặt lại mật khẩu.</p>"
                + "<p>Nhấn vào <a href='" + resetLink + "'>liên kết này</a> để đặt lại mật khẩu.</p>"
                + "<p>Liên kết sẽ hết hạn sau 1 giờ.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("duonghoangsamet@gmail.com", "Apple Store");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

