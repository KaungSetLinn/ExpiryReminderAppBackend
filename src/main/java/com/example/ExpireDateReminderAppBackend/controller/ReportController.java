package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.FoodReportDto;
import com.example.ExpireDateReminderAppBackend.dto.PredictionRequest;
import com.example.ExpireDateReminderAppBackend.dto.PredictionResponse;
import com.example.ExpireDateReminderAppBackend.service.FoodService;
import com.example.ExpireDateReminderAppBackend.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private ForecastService forecastService;

    @GetMapping("/{userId}")
    public ResponseEntity<FoodReportDto> getFoodReport(@PathVariable("userId") Long userId) {
        FoodReportDto report = foodService.getFoodReport(userId);

        return ResponseEntity.ok(report);
    }

    @PostMapping("/predict")
    public PredictionResponse predict(@RequestBody PredictionRequest request) {
        return forecastService.analyzePrediction(request);
    }
}
