package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.FrequentlyBuyFoodDto;
import com.example.ExpireDateReminderAppBackend.service.FrequentlyBuyFoodService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api/frequently-buy-foods")
@RequiredArgsConstructor
public class FrequentlyBuyFoodController {
    private final FrequentlyBuyFoodService frequentlyBuyFoodService;

    @GetMapping
    public ResponseEntity<List<FrequentlyBuyFoodDto>> getAllFrequentlyBuyFood(@RequestParam Long userId) {
        List<FrequentlyBuyFoodDto> frequentlyBuyFoodDtos = frequentlyBuyFoodService.getAllFrequentlyBuyFoodByUserId(userId);

        return ResponseEntity.ok(frequentlyBuyFoodDtos);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<FrequentlyBuyFoodDto> create(
            @RequestPart("data") FrequentlyBuyFoodDto frequentlyBuyFoodDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        FrequentlyBuyFoodDto savedDto = frequentlyBuyFoodService.saveFrequentlyBuyFood(frequentlyBuyFoodDto, file);

        return ResponseEntity.ok(savedDto);
    }
}
