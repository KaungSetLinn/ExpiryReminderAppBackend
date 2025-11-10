package com.example.ExpireDateReminderAppBackend.controller;

import com.example.ExpireDateReminderAppBackend.dto.UpdateUserRequest;
import com.example.ExpireDateReminderAppBackend.dto.UserDto;
import com.example.ExpireDateReminderAppBackend.dto.UserResponseDto;
import com.example.ExpireDateReminderAppBackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /** Update username */
    @PutMapping("/{id}/username")
    public ResponseEntity<UserResponseDto> updateUsername(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        if (request.getUsername() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserResponseDto updatedUser = userService.updateUsername(id, request.getUsername());

        return ResponseEntity.ok(updatedUser);
    }

    /** Update password */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        if (request.getCurrentPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "現在のパスワードと新しいパスワードを指定してください。"));
        }

        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        boolean isUpdated = userService.updatePassword(id, currentPassword, newPassword);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "現在のパスワードは正しくありません。"));
        }

        return ResponseEntity.ok(Map.of("message", "パスワードが正常に更新されました。"));
    }

}
