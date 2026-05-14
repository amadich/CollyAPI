package com.amadich.colly.chat.controller;

import com.amadich.colly.chat.dto.RoomRequest;
import com.amadich.colly.chat.dto.RoomResponse;
import com.amadich.colly.chat.service.RoomService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(
            @RequestBody RoomRequest request,
            Authentication authentication
    ) {

        return roomService.createRoom(
                request,
                authentication
        );
    }

    @GetMapping
    public List<RoomResponse> getRooms(
            Authentication authentication
    ) {

        return roomService.getRooms(authentication);
    }

    @PutMapping("/{roomId}")
    public RoomResponse updateRoom(
            @PathVariable UUID roomId,
            @RequestBody RoomRequest request,
            Authentication authentication
    ) {

        return roomService.updateRoom(
                roomId,
                request,
                authentication
        );
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(
            @PathVariable UUID roomId,
            Authentication authentication
    ) {

        roomService.deleteRoom(
                roomId,
                authentication
        );
    }
}