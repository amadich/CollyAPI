package com.amadich.colly.chat.controller;

import com.amadich.colly.chat.dto.ChatMessageResponse;
import com.amadich.colly.chat.entity.Message;
import com.amadich.colly.chat.entity.Room;
import com.amadich.colly.chat.repository.MessageRepository;
import com.amadich.colly.chat.repository.RoomMemberRepository;
import com.amadich.colly.chat.repository.RoomRepository;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    private final RoomMemberRepository roomMemberRepository;

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable UUID roomId,
            Authentication authentication
    ) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Room room = roomRepository.findById(roomId)
                .orElseThrow();

        boolean allowed =
                !room.isPrivate()
                        ||
                        roomMemberRepository.existsByRoomIdAndUserId(
                                roomId,
                                user.getId()
                        );

        if (!allowed) {
            throw new RuntimeException("Forbidden");
        }

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
                .username(message.getSender().getUsername())
                .firstName(message.getSender().getFirstName())
                .lastName(message.getSender().getLastName())
                .avatar(message.getSender().getAvatar())
                .content(message.getContent())
                .messageType(message.getMessageType().name())
                .mediaUrl(message.getMediaUrl())
                .createdAt(message.getCreatedAt().toString())
                .build();
    }
}