package com.example.ExpireDateReminderAppBackend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    public String welcome(@AuthenticationPrincipal String userId) {
        return "Welcome, user " + userId;
    }
}
