package com.amadich.colly.chat.websocket;

import com.amadich.colly.chat.service.RoomPresenceService;

import com.amadich.colly.user.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final RoomPresenceService roomPresenceService;

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Object roomIdObj =
                accessor.getSessionAttributes().get("roomId");

        Object userObj =
                accessor.getSessionAttributes().get("user");

        if (roomIdObj == null || userObj == null) {
            return;
        }

        UUID roomId = (UUID) roomIdObj;

        User user = (User) userObj;

        roomPresenceService.leaveRoom(roomId, user);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId + "/users",
                roomPresenceService.getUsers(roomId)
        );

        System.out.println(user.getUsername() + " disconnected");
    }
}