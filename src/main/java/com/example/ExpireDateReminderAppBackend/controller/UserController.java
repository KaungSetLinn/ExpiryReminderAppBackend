package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.UserDto;
import com.example.ExpireDateReminderAppBackend.dto.UserResponseDto;
import com.example.ExpireDateReminderAppBackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long userId) {
        UserResponseDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(userDto);
    }
}
