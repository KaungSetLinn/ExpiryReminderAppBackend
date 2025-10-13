package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.CategoryDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import com.example.ExpireDateReminderAppBackend.mapper.CategoryMapper;
import com.example.ExpireDateReminderAppBackend.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        Category savedCategory = categoryService.saveCategory(categoryDto);

        CategoryDto responseDto = categoryMapper.toDto(savedCategory);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("id") Long categoryId) {
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);

        return ResponseEntity
                .ok()
                .body(categoryDto);
    }
}
