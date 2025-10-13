package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.FrequentlyBuyFoodDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import com.example.ExpireDateReminderAppBackend.entity.FrequentlyBuyFood;
import com.example.ExpireDateReminderAppBackend.entity.User;
import com.example.ExpireDateReminderAppBackend.mapper.FrequentlyBuyFoodMapper;
import com.example.ExpireDateReminderAppBackend.repository.CategoryRepository;
import com.example.ExpireDateReminderAppBackend.repository.FrequentlyBuyFoodRepository;
import com.example.ExpireDateReminderAppBackend.repository.UserRepository;
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

    public List<FrequentlyBuyFoodDto> getAllFrequentlyBuyFoodByUserId(Long userId) {
        return frequentlyBuyFoodRepository.findByUser_Id(userId).stream()
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
            String fileName = saveFile(file);
            frequentlyBuyFood.setFoodImageUrl("/uploads/" + fileName);
        }

        // 🔹 Save to DB
        FrequentlyBuyFood savedFrequentlyBuyFood = frequentlyBuyFoodRepository.save(frequentlyBuyFood);
        return frequentlyBuyFoodMapper.toDto(savedFrequentlyBuyFood);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
