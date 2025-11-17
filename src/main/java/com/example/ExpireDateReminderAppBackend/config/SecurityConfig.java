package com.example.ExpireDateReminderAppBackend.config;

import com.example.ExpireDateReminderAppBackend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    // ✅ Constructor injection of JwtUtil
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/register",
                                "/login",
                                "/refresh",
                                "/api/auth/session",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/reset-password/**"
                        ).permitAll()
                        .requestMatchers("/api/departments/**").permitAll()
                        .anyRequest().authenticated()
                )
                // ✅ Use filter bean that knows about jwtUtil
                .addFilterBefore(jwtCookieFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.disable());

        return http.build();
    }

    // ✅ Register JwtCookieFilter as a Spring Bean so it can use jwtUtil
    @Bean
    public JwtCookieFilter jwtCookieFilter() {
        return new JwtCookieFilter(jwtUtil);
    }

    // ✅ CORS configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ✅ JwtCookieFilter now depends on JwtUtil (injected)
    static class JwtCookieFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;

        public JwtCookieFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws java.io.IOException, jakarta.servlet.ServletException {

            // Allow unauthenticated paths (avoid blocking /login, /register, /refresh)
            String path = request.getRequestURI();
            if (path.equals("/login") || path.equals("/register") || path.equals("/refresh")
                    || path.equals("/auth/forgot-password") || path.startsWith("/auth/reset-password")) {
                chain.doFilter(request, response);
                return;
            }

            Cookie[] cookies = request.getCookies();
            String accessToken = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("ACCESS_TOKEN".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (accessToken != null) {
                String userId = jwtUtil.validateToken(accessToken);
                if (userId != null) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userId, null, java.util.Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    chain.doFilter(request, response);
                    return;
                } else {
                    // ❌ Token invalid or expired → 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            // ❌ No token → 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
