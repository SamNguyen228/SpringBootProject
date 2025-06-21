// UserController.java
package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserManageController {

    @Autowired
    private UserRepository userRepository;

   @GetMapping("")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> usersPage = userRepository.findAll(pageable);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("pageTitle", "Quản lí người dùng");

        return "view/admin/userManage/user-manage";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Tạo mới người dùng");
        return "view/admin/userManage/edit-create-user";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "view/admin/user/edit-create-user";
        }
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user.get());
        model.addAttribute("pageTitle", "Cập nhật người dùng");
        return "view/admin/userManage/edit-create-user";
    }

  @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "view/admin/userManage/edit-create-user";
        }

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        User existingUser = existingUserOpt.get();

        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress()); 
        existingUser.setIsLocked(user.getIsLocked());  

        userRepository.save(existingUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
} 
