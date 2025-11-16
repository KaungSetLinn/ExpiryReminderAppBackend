package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.FoodReportDto;
import com.example.ExpireDateReminderAppBackend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private FoodService foodService;

    @GetMapping("/{userId}")
    public ResponseEntity<FoodReportDto> getFoodReport(@PathVariable("userId") Long userId) {
        FoodReportDto report = foodService.getFoodReport(userId);

        return ResponseEntity.ok(report);
    }
}
