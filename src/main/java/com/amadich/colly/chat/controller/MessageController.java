package com.amadich.colly.chat.controller;

import com.amadich.colly.chat.dto.ChatMessageResponse;
import com.amadich.colly.chat.entity.Message;
import com.amadich.colly.chat.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable UUID roomId
    ) {

        return messageRepository
                .findByRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ChatMessageResponse mapToResponse(
            Message message
    ) {

        return ChatMessageResponse.builder()
                .id(message.getId().toString())
                .username(
                        message
                                .getSender()
                                .getUsername()
                )
                .avatar(
                        message
                                .getSender()
                                .getAvatar()
                )
                .content(message.getContent())
                .messageType(
                        message
                                .getMessageType()
                                .name()
                )
                .mediaUrl(message.getMediaUrl())
                .createdAt(
                        message
                                .getCreatedAt()
                                .toString()
                )
                .build();
    }
}