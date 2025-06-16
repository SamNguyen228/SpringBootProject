package com.example.ecommerce.service;

import com.example.ecommerce.model.viewmodel.CategoriesVM;
import com.example.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service 
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoriesVM> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoriesVM(
                        c.getCategoryId(),
                        c.getCategoryName(),
                        c.getProducts().size()
                ))
                .sorted((a, b) -> a.getTenLoai().compareTo(b.getTenLoai()))
                .collect(Collectors.toList());
    }
}
