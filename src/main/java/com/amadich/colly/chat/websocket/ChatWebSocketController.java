package com.amadich.colly.chat.websocket;

import com.amadich.colly.chat.dto.ChatMessageRequest;
import com.amadich.colly.chat.dto.ChatMessageResponse;
import com.amadich.colly.chat.dto.JoinRoomRequest;
import com.amadich.colly.chat.entity.Message;
import com.amadich.colly.chat.entity.Room;
import com.amadich.colly.chat.enums.MessageType;
import com.amadich.colly.chat.repository.MessageRepository;
import com.amadich.colly.chat.repository.RoomRepository;
import com.amadich.colly.chat.service.RoomPresenceService;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final RoomPresenceService roomPresenceService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request, Authentication authentication) {

        String email = authentication.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow();

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow();

        Message message = Message.builder()
                .room(room)
                .sender(sender)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .messageType(
                        MessageType.valueOf(request.getMessageType())
                )
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        ChatMessageResponse response = ChatMessageResponse.builder()
                        .id(message.getId().toString())
                        .username(sender.getUsername())
                        .firstName(sender.getFirstName())
                        .lastName(sender.getLastName())
                        .avatar(sender.getAvatar())
                        .content(message.getContent())
                        .messageType(message.getMessageType().name())
                        .mediaUrl(message.getMediaUrl())
                        .createdAt(message.getCreatedAt().toString())
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/room/" + room.getId(),
                response
        );
    }


    @MessageMapping("/chat.join")
    public void joinRoom(
            JoinRoomRequest request,
            Authentication authentication,
            org.springframework.messaging.simp.SimpMessageHeaderAccessor headerAccessor
    ) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        roomPresenceService.joinRoom(request.getRoomId(), user);

        // SAVE DATA INSIDE WS SESSION
        headerAccessor.getSessionAttributes().put("roomId", request.getRoomId());

        headerAccessor.getSessionAttributes().put("user", user);

        messagingTemplate.convertAndSend(
                "/topic/room/" + request.getRoomId() + "/users",
                roomPresenceService.getUsers(request.getRoomId())
        );
    }
}