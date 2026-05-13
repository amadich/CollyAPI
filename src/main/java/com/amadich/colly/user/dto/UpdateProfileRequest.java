package com.amadich.colly.user.dto;

import com.amadich.colly.user.enums.Gender;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Integer age;

    private Gender gender;

    private String currentPassword;

    private String newPassword;
}