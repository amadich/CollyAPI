package com.amadich.colly.chat.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatMessageRequest {

    private UUID roomId;

    private String content;

    private String messageType;

    private String mediaUrl;
}