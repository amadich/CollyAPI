package com.amadich.colly.auth.dto;

import com.amadich.colly.user.enums.Gender;
import com.amadich.colly.user.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    private Integer age;

    private Role role;

    private Gender gender;

    private String avatar;
}