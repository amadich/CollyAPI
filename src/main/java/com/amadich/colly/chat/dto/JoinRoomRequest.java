package com.amadich.colly.chat.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class JoinRoomRequest {
    private UUID roomId;
}