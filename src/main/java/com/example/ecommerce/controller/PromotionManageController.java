package com.example.ecommerce.controller;

import com.example.ecommerce.model.Promotion;
import com.example.ecommerce.repository.PromotionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/promotions")
public class PromotionManageController {
    @Autowired
    private PromotionRepository promotionRepository;

    @GetMapping
    public String listPromotions(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("promotionId").descending());

        Page<Promotion> promotionsPage;

        if (keyword != null && !keyword.isEmpty()) {
            promotionsPage = promotionRepository.findByPromotionNameContainingIgnoreCase(keyword, pageable);
        } else if (startDate != null && endDate != null) {
            promotionsPage = promotionRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59),
                    pageable);
        } else {
            promotionsPage = promotionRepository.findAll(pageable);
        }

        model.addAttribute("promotionsPage", promotionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promotionsPage.getTotalPages());
        model.addAttribute("pageTitle", "Quản lí khuyến mãi");
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "view/admin/promotionManage/promotion-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("pageTitle", "Thêm mới khuyến mãi");
        return "view/admin/promotionManage/edit-create-promotion";
    }

    @PostMapping("/save")
    public String savePromotion(@ModelAttribute Promotion promotion) {
        promotionRepository.save(promotion);
        return "redirect:/admin/promotions";
    }

    @GetMapping("/edit/{id}")
    public String editPromotion(@PathVariable("id") Integer id, Model model) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        if (promotion.isPresent()) {
            model.addAttribute("promotion", promotion.get());
            model.addAttribute("pageTitle", "Cập nhật khuyến mãi");
            return "view/admin/promotionManage/edit-create-promotion";
        } else {
            return "redirect:/admin/promotions";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePromotion(@PathVariable("id") Integer id) {
        promotionRepository.deleteById(id);
        return "redirect:/admin/promotions";
    }
}
