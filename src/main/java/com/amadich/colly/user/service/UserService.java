package com.amadich.colly.user.service;

import com.amadich.colly.user.dto.UpdateProfileRequest;
import com.amadich.colly.user.dto.UserResponse;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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


    public UserResponse updateProfile(
            Authentication authentication,
            UpdateProfileRequest request
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        // EMAIL CHECK
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException("Email already used");
        }

        // USERNAME CHECK
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new RuntimeException("Username already used");
        }

        // UPDATE FIELDS
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setGender(request.getGender());

        // PASSWORD UPDATE
        if (request.getNewPassword() != null
                && !request.getNewPassword().isBlank()) {

            boolean matches = passwordEncoder.matches(
                    request.getCurrentPassword(),
                    user.getPassword()
            );

            if (!matches) {
                throw new RuntimeException("Current password incorrect");
            }

            user.setPassword(
                    passwordEncoder.encode(request.getNewPassword())
            );
        }

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