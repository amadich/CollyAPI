package com.amadich.colly.chat.service;

import com.amadich.colly.chat.dto.RoomRequest;
import com.amadich.colly.chat.dto.RoomResponse;
import com.amadich.colly.chat.dto.RoomUserResponse;
import com.amadich.colly.chat.entity.Room;
import com.amadich.colly.chat.repository.RoomRepository;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;
    private final RoomPresenceService roomPresenceService;

    public RoomResponse createRoom(RoomRequest request, Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPrivate(request.getIsPrivate())
                .maxMembers(request.getMaxMembers())
                .owner(user)
                .build();

        Room savedRoom = roomRepository.save(room);

        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .description(savedRoom.getDescription())
                .isPrivate(savedRoom.isPrivate())
                .maxMembers(savedRoom.getMaxMembers())
                .createdAt(savedRoom.getCreatedAt())
                .build();
    }

    public List<RoomResponse> getRooms() {

        List<Room> rooms = roomRepository.findAll();

        if (rooms.isEmpty()) {
            return List.of();
        }

        return rooms.stream()
                .map(room -> {

                    List<RoomUserResponse> users =
                            roomPresenceService
                                    .getRoomUsers(room.getId())
                                    .stream()
                                    .map(user ->
                                            RoomUserResponse.builder()
                                                    .username(user.getUsername())
                                                    .avatar(user.getAvatar())
                                                    .build()
                                    )
                                    .toList();

                    return RoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .description(room.getDescription())
                            .isPrivate(room.isPrivate())
                            .maxMembers(room.getMaxMembers())
                            .onlineCount(users.size())
                            .users(users)
                            .createdAt(room.getCreatedAt())
                            .build();
                })
                .toList();
    }
}