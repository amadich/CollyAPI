package com.amadich.colly.friend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FriendResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String username;

    private String avatar;
}