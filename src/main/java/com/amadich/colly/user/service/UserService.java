package com.amadich.colly.user.service;

import com.amadich.colly.user.dto.UserResponse;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse updateAvatar(
            Authentication authentication,
            String avatar
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.setAvatar(avatar);

        userRepository.save(user);

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
}