package com.amadich.colly.auth.controller;

import com.amadich.colly.auth.dto.AuthResponse;
import com.amadich.colly.auth.dto.LoginRequest;
import com.amadich.colly.auth.dto.RegisterRequest;
import com.amadich.colly.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

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
    public void logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void addJwtCookie(HttpServletResponse response, String token) {

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}