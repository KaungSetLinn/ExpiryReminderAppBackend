package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.FoodDto;
import com.example.ExpireDateReminderAppBackend.dto.FoodReportDto;
import com.example.ExpireDateReminderAppBackend.entity.Category;
import com.example.ExpireDateReminderAppBackend.entity.Food;
import com.example.ExpireDateReminderAppBackend.entity.Status;
import com.example.ExpireDateReminderAppBackend.entity.User;
import com.example.ExpireDateReminderAppBackend.mapper.FoodMapper;
import com.example.ExpireDateReminderAppBackend.repository.CategoryRepository;
import com.example.ExpireDateReminderAppBackend.repository.FoodRepository;
import com.example.ExpireDateReminderAppBackend.repository.StatusRepository;
import com.example.ExpireDateReminderAppBackend.repository.UserRepository;
import com.example.ExpireDateReminderAppBackend.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 0H02009_カゥンセッリン
 */
@Service
public class FoodService {

//    @Autowired tells Spring:
//    “Find a suitable bean of this type in the application context and inject it here automatically.”
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private FoodMapper foodMapper;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Long activeStatusId = 1L;
    private Long consumedStatusId = 2L;
    private Long discardedStatusId = 3L;

    public List<FoodDto> getAllFoodByUserId(Long userId) {
        List<Food> foods = foodRepository.findByUser_Id(userId);

        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getActiveFoodByUserId(Long userId) {
        List<Food> foods = foodRepository.findByUser_IdAndStatus_StatusIdOrderByExpireDateAsc(userId, activeStatusId);

        return foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FoodDto> getGroupedAndSortedFoodByUserId(Long userId) {
        List<Food> foods = foodRepository.findByUser_IdAndStatus_StatusIdOrderByExpireDateAsc(userId, activeStatusId);

        // Convert to Dto
        List<FoodDto> foodDtos = foods.stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());

        // Group by food name
        Map<String, List<FoodDto>> foodsGroupedByName = foodDtos.stream()
                .collect(Collectors.groupingBy(FoodDto::getFoodName));

        return foodsGroupedByName.values().stream()
                .sorted(Comparator.comparing(group -> group.stream()
                        .map(FoodDto::getExpireDate)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.MAX)))
                .flatMap(group -> group.stream()
                        .sorted(Comparator.comparing(FoodDto::getExpireDate)))
                .collect(Collectors.toList());
    }

    public List<FoodDto> getUniqueFoodByUserId(Long userId) {
        List<Food> foods = foodRepository.findByUser_IdOrderByFoodIdDesc(userId);

        // Deduplicate by foodName — keep the first item (earliest expireDate)
        Map<String, Food> uniqueFoods = foods.stream()
                .collect(Collectors.toMap(
                        Food::getFoodName,
                        food -> food,
                        (existing, duplicate) -> existing, // keep the first one
                        LinkedHashMap::new                            // ✅ preserve order
                ));

        return uniqueFoods.values().stream()
                .map(foodMapper::toDto)
                .collect(Collectors.toList());
    }

    public FoodDto saveFood(FoodDto foodDto, MultipartFile file) throws IOException {
        User user = userRepository.findById(foodDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(foodDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Status status = statusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        // 🔹 Convert DTO → Entity
        Food food = foodMapper.toEntity(foodDto);
        food.setUser(user);
        food.setCategory(category);
        food.setStatus(status);

        // 🔹 Handle file upload
        if (file != null && !file.isEmpty()) {
            String fileName = FileUploadUtil.saveFile(file, uploadDir);
            food.setFoodImageUrl("/uploads/" + fileName);
        }

        // 🔹 Save to DB
        Food savedFood = foodRepository.save(food);

        // 🔹 Convert back to DTO
        return foodMapper.toDto(savedFood);
    }

    public FoodDto discardFood(Long foodId) {
        // Find the food by ID
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodId));

        // Find the "discarded" status (assuming its ID is 3L, adjust as needed)
        Status discardedStatus = statusRepository.findById(discardedStatusId)
                .orElseThrow(() -> new RuntimeException("Discarded status not found"));

        // Update the food's status
        food.setStatus(discardedStatus);

        // Save the food
        Food updatedFood = foodRepository.save(food);

        // Return DTO
        return foodMapper.toDto(updatedFood);
    }

    public FoodDto consumeFood(FoodDto foodDto) {
        Food food = foodRepository.findById(foodDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodDto.getFoodId()));

        // Update the current contents
        food.setCurrentContents(foodDto.getCurrentContents());

        // If amount reaches 0, set status to “consumed”
        if (food.getCurrentContents() <= 0) {
            Status consumedStatus = statusRepository.findById(consumedStatusId)
                    .orElseThrow(() -> new RuntimeException("Status not found"));

            food.setStatus(consumedStatus);
        }

        // saved the updated food in the database
        Food savedFood = foodRepository.save(food);

        return foodMapper.toDto(savedFood);
    }

    public FoodReportDto getFoodReport(Long userId) {
        Status consumedStatus = statusRepository.findById(consumedStatusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Status discardedStatus = statusRepository.findById(discardedStatusId)
                .orElseThrow(() -> new RuntimeException("Discarded status not found"));

        List<Food> foods = foodRepository.findByUser_Id(userId);

        // ✅ Count by comparing with Status entities
        long consumedCount = foods.stream()
                .filter(food -> food.getStatus() != null && food.getStatus().getStatusId().equals(consumedStatus.getStatusId()))
                .count();

        long wastedCount = foods.stream()
                .filter(food -> food.getStatus() != null && food.getStatus().getStatusId().equals(discardedStatus.getStatusId()))
                .count();

        // ✅ Waste by category
        Map<String, Long> wasteByCategory = foods.stream()
                .filter(food -> food.getStatus() != null && food.getStatus().getStatusId().equals(discardedStatus.getStatusId()))
                .collect(Collectors.groupingBy(food -> food.getCategory().getCategoryName(), Collectors.counting()));

        // ✅ Extract months dynamically
        List<String> months = foods.stream()
                .map(Food::getExpireDate)
                .filter(Objects::nonNull)
                .sorted()
                .map(date -> date.getMonthValue() + "月")
                .distinct()
                .collect(Collectors.toList());

        // ✅ Monthly trend
        Map<Integer, Long> consumedPerMonth = foods.stream()
                .filter(f -> f.getStatus() != null && f.getStatus().getStatusId().equals(consumedStatus.getStatusId()))
                .filter(f -> f.getExpireDate() != null)
                .collect(Collectors.groupingBy(f -> f.getExpireDate().getMonthValue(), Collectors.counting()));

        Map<Integer, Long> wastedPerMonth = foods.stream()
                .filter(f -> f.getStatus() != null && f.getStatus().getStatusId().equals(discardedStatus.getStatusId()))
                .filter(f -> f.getExpireDate() != null)
                .collect(Collectors.groupingBy(f -> f.getExpireDate().getMonthValue(), Collectors.counting()));

        List<Integer> consumedTrend = months.stream()
                .map(m -> {
                    int monthNum = Integer.parseInt(m.replace("月", ""));
                    return consumedPerMonth.getOrDefault(monthNum, 0L).intValue();
                })
                .collect(Collectors.toList());

        List<Integer> wastedTrend = months.stream()
                .map(m -> {
                    int monthNum = Integer.parseInt(m.replace("月", ""));
                    return wastedPerMonth.getOrDefault(monthNum, 0L).intValue();
                })
                .collect(Collectors.toList());

        return FoodReportDto.builder()
                .consumedCount((int) consumedCount)
                .wastedCount((int) wastedCount)
                .wasteByCategory(wasteByCategory)
                .months(months)
                .consumedTrend(consumedTrend)
                .wastedTrend(wastedTrend)
                .build();
    }
}
