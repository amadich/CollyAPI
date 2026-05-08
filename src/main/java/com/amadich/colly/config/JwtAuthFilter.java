package com.amadich.colly.config;

import com.amadich.colly.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private String getTokenFromRequest(HttpServletRequest request) {

        // 1. From Header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. From Cookie
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                var authorities = java.util.List.of(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + role
                        )
                );

                var auth = new UsernamePasswordAuthenticationToken(
                        email,   // principal (NO DB USER OBJECT)
                        null,
                        authorities
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);
            }

        } catch (Exception e) {
            // invalid token → ignore authentication
        }

        filterChain.doFilter(request, response);
    }
}
