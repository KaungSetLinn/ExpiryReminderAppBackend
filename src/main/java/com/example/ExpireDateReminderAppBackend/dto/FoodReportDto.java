package com.example.ExpireDateReminderAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 0H02009_カゥンセッリン
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodReportDto {
    private int consumedCount;
    private int wastedCount;
    private Map<String, Long> wasteByCategory;
    private List<String> months;
    private List<Integer> consumedTrend;
    private List<Integer> wastedTrend;
}
