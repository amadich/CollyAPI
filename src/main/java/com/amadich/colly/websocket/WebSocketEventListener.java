package com.amadich.colly.websocket;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final OnlineUserService onlineUserService;

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Authentication auth = (Authentication) headerAccessor.getUser();

        if (auth != null) {

            onlineUserService.addUser(auth.getName());

            messagingTemplate.convertAndSend(
                    "/topic/online-users",
                    onlineUserService.getOnlineUsersCount()
            );
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Authentication auth = (Authentication) headerAccessor.getUser();

        if (auth != null) {

            onlineUserService.removeUser(auth.getName());

            messagingTemplate.convertAndSend(
                    "/topic/online-users",
                    onlineUserService.getOnlineUsersCount()
            );
        }
    }
}