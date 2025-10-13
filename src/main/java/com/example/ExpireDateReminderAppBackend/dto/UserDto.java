package com.example.ExpireDateReminderAppBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 0H02009_カゥンセッリン
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// used for input (registration/login)
public class UserDto {
    private Long id;
    private String username;
    private String password;
}
