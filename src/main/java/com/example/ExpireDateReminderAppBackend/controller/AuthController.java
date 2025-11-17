package com.example.ExpireDateReminderAppBackend.controller;
import com.example.ExpireDateReminderAppBackend.dto.ForgotPasswordRequest;
import com.example.ExpireDateReminderAppBackend.dto.ResetPasswordRequest;
import com.example.ExpireDateReminderAppBackend.dto.UserDto;
import com.example.ExpireDateReminderAppBackend.dto.UserResponseDto;
import com.example.ExpireDateReminderAppBackend.entity.User;
import com.example.ExpireDateReminderAppBackend.mapper.UserMapper;
import com.example.ExpireDateReminderAppBackend.repository.UserRepository;
import com.example.ExpireDateReminderAppBackend.service.UserService;
import com.example.ExpireDateReminderAppBackend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 0H02009_カゥンセッリン
 */

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);

        UserResponseDto responseDto = userMapper.toResponseDto(savedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto,
                        HttpServletResponse response) {

        List<User> users = userRepository.findByUsername(userDto.getUsername());
        for (User user : users) {
            String storedPassword = user.getPassword();

            if (passwordEncoder.matches(userDto.getPassword(), storedPassword)) {
                String accessToken = jwtUtil.generateAccessToken(user.getId().toString());
                String refreshToken = jwtUtil.generateRefreshToken(user.getId().toString());

                // ACCESS TOKEN: 1 hour
                Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setPath("/");
                accessCookie.setMaxAge(60 * 60); // 1 hour
                response.addCookie(accessCookie);

                // REFRESH TOKEN: 7 days
                Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                response.addCookie(refreshCookie);

                // ✅ Return a typed DTO instead of Map
                UserResponseDto responseDto = UserResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build();

                return ResponseEntity.ok(responseDto);
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");
        }

        String userId = jwtUtil.validateToken(refreshToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(accessCookie);

        return ResponseEntity.ok(Map.of("status", "refreshed"));
    }

    @GetMapping("/api/users/current")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "ACCESS_TOKEN", required = false) String accessToken) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No access token"));
        }

        String userId = jwtUtil.validateToken(accessToken);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }

        Optional<User> user = userRepository.findById(Long.parseLong(userId));

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        return  ResponseEntity.ok(Map.of(
                "id", user.get().getId(),
                "username", user.get().getUsername()
        ));
    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return "Logged out";
    }

    @GetMapping("/api/auth/session")
    public ResponseEntity<?> getSession(@CookieValue(name = "ACCESS_TOKEN", required = false) String accessToken) {
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No access token");
        }

        String userId = jwtUtil.validateToken(accessToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        return ResponseEntity.ok(Map.of("userId", userId));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String result = userService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        String result = userService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(result);
    }

}
