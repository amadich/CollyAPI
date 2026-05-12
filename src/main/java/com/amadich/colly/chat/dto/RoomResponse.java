package com.amadich.colly.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RoomResponse {

    private UUID id;

    private String name;

    private String description;

    private Boolean isPrivate;

    private Integer maxMembers;

    private Integer onlineCount;

    private List<RoomUserResponse> users;

    private LocalDateTime createdAt;
}