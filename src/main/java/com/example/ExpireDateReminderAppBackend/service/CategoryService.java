package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.CategoryDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import com.example.ExpireDateReminderAppBackend.mapper.CategoryMapper;
import com.example.ExpireDateReminderAppBackend.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 0H02009_カゥンセッリン
 */

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public Category saveCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);

        return categoryRepository.save(category);
    }

    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category with id " + categoryId + "not found"));

        return categoryMapper.toDto(category);
    }
}
