package com.amadich.colly.auth.controller;

import com.amadich.colly.auth.dto.AuthResponse;
import com.amadich.colly.auth.dto.LoginRequest;
import com.amadich.colly.auth.dto.RegisterRequest;
import com.amadich.colly.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request, HttpServletResponse response) {

        AuthResponse authResponse = authService.register(request);

        addJwtCookie(response, authResponse.getToken());

        return authResponse;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {

        AuthResponse authResponse = authService.login(request);

        addJwtCookie(response, authResponse.getToken());

        return authResponse;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", "");

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return Map.of(
                "message",
                "Logged out successfully"
        );
    }

    private void addJwtCookie(HttpServletResponse response, String token) {

        Cookie cookie = new Cookie("jwt", token);

        cookie.setHttpOnly(true);

        cookie.setSecure(false);
        // TRUE in production HTTPS

        cookie.setPath("/");

        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);
    }
}