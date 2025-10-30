package com.example.ExpireDateReminderAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 0H02009_カゥンセッリン
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDto {
    private Long foodId;
    private String foodName;
    private Long userId;
    private Long categoryId;
    private String categoryName;   // optional convenience field
    private Long statusId;
    private String statusName;     // optional convenience field
    private Integer quantity;
    private String unit;
    private Integer totalContents;
    private Integer currentContents;
    private String contentUnit;
    private LocalDate expireDate;
    private String memo;
    private String foodImageUrl;
}
