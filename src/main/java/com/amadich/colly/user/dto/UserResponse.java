package com.amadich.colly.user.dto;

import com.amadich.colly.user.enums.Gender;
import com.amadich.colly.user.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Integer age;

    private Role role;

    private Gender gender;

    private String avatar;
}