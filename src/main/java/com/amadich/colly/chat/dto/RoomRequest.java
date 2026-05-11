package com.amadich.colly.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    private String name;

    private String description;

    private Boolean isPrivate;

    private Integer maxMembers;
}