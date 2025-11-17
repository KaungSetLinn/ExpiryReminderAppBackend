package com.example.ExpireDateReminderAppBackend.service;

import com.example.ExpireDateReminderAppBackend.dto.UserDto;
import com.example.ExpireDateReminderAppBackend.dto.UserResponseDto;
import com.example.ExpireDateReminderAppBackend.entity.User;
import com.example.ExpireDateReminderAppBackend.mapper.UserMapper;
import com.example.ExpireDateReminderAppBackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 0H02009_カゥンセッリン
 */

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id" + userId + " is not found"));

        return userMapper.toResponseDto(user);
    }

    // Update username
    public UserResponseDto updateUsername(Long userId, String username) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id" + userId + " is not found"));

        user.setUsername(username);
        userRepository.save(user);

        return userMapper.toResponseDto(user);
    }

    /** Update password */
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public String forgotPassword(String email) {

        Optional<User> optional = userRepository.findByEmail(email);

        // 👇 結果は常に同じ（ユーザー存在確認を避けるため）
        if (optional.isEmpty()) {
            return "メールが存在する場合、パスワード再設定用のリンクを送信しました。";
        }

        User user = optional.get();

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(20));

        userRepository.save(user);

        // 👇 Change if needed
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        String body =
                "以下のリンクからパスワードの再設定を行ってください。\n\n"
                        + resetUrl
                        + "\n\nこのリンクの有効期限は20分です。";

        emailService.sendEmail(user.getEmail(), "【ExpiryTracker】パスワード再設定のご案内", body);

        return "メールが存在する場合、パスワード再設定用のリンクを送信しました。";
    }

    public String resetPassword(String token, String newPassword) {

        Optional<User> optional = userRepository.findByResetToken(token);

        if (optional.isEmpty()) {
            return "無効なトークンです。再度お試しください。";
        }

        User user = optional.get();

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return "トークンの有効期限が切れています。再度パスワード再設定を行ってください。";
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        // トークンを無効化
        user.setResetToken(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        return "パスワードの更新が完了しました。ログインし直してください。";
    }
}
