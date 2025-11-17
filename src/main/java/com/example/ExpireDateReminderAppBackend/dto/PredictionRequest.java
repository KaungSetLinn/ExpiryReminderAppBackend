package com.example.ExpireDateReminderAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 0H02009_カゥンセッリン
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PredictionRequest {
    private double[] consumedHistory;
    private double[] wastedHistory;
    private int months;

    private Map<String, Integer> wasteByCategory;
}
