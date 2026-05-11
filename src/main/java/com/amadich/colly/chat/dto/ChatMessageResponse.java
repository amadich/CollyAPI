package com.amadich.colly.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponse {

    private String id;

    private String username;

    private String firstName;

    private String lastName;

    private String avatar;

    private String content;

    private String messageType;

    private String mediaUrl;

    private String createdAt;
}