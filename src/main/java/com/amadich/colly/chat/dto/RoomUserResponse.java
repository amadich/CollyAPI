package com.amadich.colly.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomUserResponse {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String avatar;
}