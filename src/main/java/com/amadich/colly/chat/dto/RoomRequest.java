package com.amadich.colly.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoomRequest {

    private String name;

    private String description;

    private Boolean isPrivate;

    private Integer maxMembers;

    private List<UUID> memberIds;
}