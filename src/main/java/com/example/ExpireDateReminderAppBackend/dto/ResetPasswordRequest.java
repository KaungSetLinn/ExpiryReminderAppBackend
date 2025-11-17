package com.example.ExpireDateReminderAppBackend.dto;

import lombok.Data;

/**
 * 0H02009_カゥンセッリン
 */

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
