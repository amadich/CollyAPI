package com.amadich.colly.websocket;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final OnlineUserService onlineUserService;

    @MessageMapping("/online")
    @SendTo("/topic/online-users")
    public int getOnlineUsers() {

        return onlineUserService.getOnlineUsersCount();
    }
}