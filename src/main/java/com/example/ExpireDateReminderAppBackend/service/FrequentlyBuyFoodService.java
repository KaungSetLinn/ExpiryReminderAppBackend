package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.FoodDto;
import com.example.ExpireDateReminderAppBackend.dto.FrequentlyBuyFoodDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import com.example.ExpireDateReminderAppBackend.entity.FrequentlyBuyFood;
import com.example.ExpireDateReminderAppBackend.entity.User;
import com.example.ExpireDateReminderAppBackend.mapper.FrequentlyBuyFoodMapper;
import com.example.ExpireDateReminderAppBackend.repository.CategoryRepository;
import com.example.ExpireDateReminderAppBackend.repository.FrequentlyBuyFoodRepository;
import com.example.ExpireDateReminderAppBackend.repository.UserRepository;
import com.example.ExpireDateReminderAppBackend.util.FileUploadUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 0H02009_カゥンセッリン
 */

@Service
@RequiredArgsConstructor
public class FrequentlyBuyFoodService {
    private final FrequentlyBuyFoodRepository frequentlyBuyFoodRepository;
    private final FrequentlyBuyFoodMapper frequentlyBuyFoodMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public FrequentlyBuyFoodDto getFrequentlyBuyFoodById(Long frequentlyBuyFoodId) {
        FrequentlyBuyFood food = frequentlyBuyFoodRepository.findById(frequentlyBuyFoodId)
                .orElseThrow(() -> new RuntimeException("Frequently buy food not found"));

        return frequentlyBuyFoodMapper.toDto(food);
    }

    public List<FrequentlyBuyFoodDto> getAllFrequentlyBuyFoodByUserId(Long userId) {
        return frequentlyBuyFoodRepository.findByUser_IdOrderByFrequentlyBuyFoodIdDesc(userId).stream()
                .map(frequentlyBuyFoodMapper::toDto)
                .collect(Collectors.toList());
    }

    public FrequentlyBuyFoodDto saveFrequentlyBuyFood(FrequentlyBuyFoodDto dto, MultipartFile file) throws IOException {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 🔹 Convert DTO → Entity
        FrequentlyBuyFood frequentlyBuyFood = frequentlyBuyFoodMapper.toEntity(dto);
        frequentlyBuyFood.setUser(user);
        frequentlyBuyFood.setCategory(category);

        // 🔹 Handle file upload
        if (file != null && !file.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(file, uploadDir);
            frequentlyBuyFood.setFoodImageUrl("/uploads/" + fileName);
        }

        // 🔹 Save to DB
        FrequentlyBuyFood savedFrequentlyBuyFood = frequentlyBuyFoodRepository.save(frequentlyBuyFood);
        return frequentlyBuyFoodMapper.toDto(savedFrequentlyBuyFood);
    }

    public FrequentlyBuyFoodDto updateFrequentlyBuyFood(Long frequentlyBuyFoodId, FrequentlyBuyFoodDto foodDto, MultipartFile file) throws IOException {
        FrequentlyBuyFood existingFood = frequentlyBuyFoodRepository.findById(frequentlyBuyFoodId)
                .orElseThrow(() -> new RuntimeException("Food not found with id " + frequentlyBuyFoodId));

        Category category = categoryRepository.findById(foodDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Update editable fields
        existingFood.setFrequentlyBuyFoodName(foodDto.getFrequentlyBuyFoodName());
        existingFood.setCategory(category);
        existingFood.setQuantity(foodDto.getQuantity());
        existingFood.setUnit(foodDto.getUnit());
        existingFood.setTotalContents(foodDto.getTotalContents());
        existingFood.setContentUnit(foodDto.getContentUnit());
        existingFood.setMemo(foodDto.getMemo());

        // If a new image file is uploaded, delete the old one and then save the new
        if (file != null && !file.isEmpty()) {
            String oldImageUrl = existingFood.getFoodImageUrl();

            FileUploadUtil.deleteOldFile(oldImageUrl, uploadDir);
            String fileName = FileUploadUtil.saveFile(file, uploadDir);
            existingFood.setFoodImageUrl("/uploads/" + fileName);
        }

        FrequentlyBuyFood updatedFood = frequentlyBuyFoodRepository.save(existingFood);
        return frequentlyBuyFoodMapper.toDto(updatedFood);
    }

    public void deleteFrequentlyBuyFood(Long frequentlyBuyFoodId) {
        // Find the existing entity
        FrequentlyBuyFood existingFood = frequentlyBuyFoodRepository.findById(frequentlyBuyFoodId)
                .orElseThrow(() -> new RuntimeException("Frequently buy food not found with id " + frequentlyBuyFoodId));

        // Delete associated image file if exists
        if (existingFood.getFoodImageUrl() != null) {
            String imageUrl = existingFood.getFoodImageUrl();

            FileUploadUtil.deleteOldFile(imageUrl, uploadDir);
        }

        // Delete the entity from database
        frequentlyBuyFoodRepository.delete(existingFood);
    }

}
