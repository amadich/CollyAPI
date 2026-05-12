package com.amadich.colly.chat.controller;

import com.amadich.colly.chat.dto.RoomRequest;
import com.amadich.colly.chat.dto.RoomResponse;
import com.amadich.colly.chat.service.RoomService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(@RequestBody RoomRequest request, Authentication authentication) {

        return roomService.createRoom(
                request,
                authentication
        );
    }

    @GetMapping
    public List<RoomResponse> getRooms() {

        return roomService.getRooms();
    }
}