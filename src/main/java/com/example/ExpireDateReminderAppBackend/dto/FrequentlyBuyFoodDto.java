package com.example.ExpireDateReminderAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * 0H02009_カゥンセッリン
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrequentlyBuyFoodDto {
    private Long frequentlyBuyFoodId;
    private String frequentlyBuyFoodName;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private Integer quantity;
    private String unit;
    private Integer totalContents;
    private String contentUnit;
    private String foodImageUrl;
    private String memo;
    private Integer reminderDaysBeforeExpire;
    private LocalTime reminderTime;
}
