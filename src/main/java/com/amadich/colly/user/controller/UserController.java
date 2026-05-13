package com.amadich.colly.user.controller;

import com.amadich.colly.user.dto.UpdateAvatarRequest;
import com.amadich.colly.user.dto.UpdateProfileRequest;
import com.amadich.colly.user.dto.UserResponse;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;
import com.amadich.colly.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .age(user.getAge())
                .role(user.getRole())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .build();
    }

    @PutMapping("/avatar")
    public UserResponse updateAvatar(Authentication authentication, @RequestBody UpdateAvatarRequest request) {

        return userService.updateAvatar(
                authentication,
                request.getAvatar()
        );
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {

        return userService.updateProfile(authentication, request);
    }
}