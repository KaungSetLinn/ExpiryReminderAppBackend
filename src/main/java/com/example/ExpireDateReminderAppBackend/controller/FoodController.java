package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.FoodDto;
import com.example.ExpireDateReminderAppBackend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodDto>> getAllFoodByUserId(@RequestParam Long userId) {
        List<FoodDto> foodDtos = foodService.getAllFoodByUserId(userId);

        return ResponseEntity.ok(foodDtos);
    }

    // ✅ Add new food (supports image upload)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<FoodDto> createFood(
            @RequestPart("data") FoodDto foodDto,
            @RequestPart(value = "file", required = false)MultipartFile file
    ) throws IOException {
        FoodDto savedFoodDto = foodService.saveFood(foodDto, file);

        return new ResponseEntity<>(savedFoodDto, HttpStatus.CREATED);
    }

}
