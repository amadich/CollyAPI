package com.amadich.colly.chat.service;

import com.amadich.colly.chat.dto.OnlineUserResponse;
import com.amadich.colly.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomPresenceService {

    private final Map<UUID, Set<User>> roomUsers = new ConcurrentHashMap<>();

    public void joinRoom(UUID roomId, User user) {
        roomUsers.computeIfAbsent(roomId,k -> ConcurrentHashMap.newKeySet()).add(user);
    }

    public void leaveRoom(UUID roomId, User user) {

        Set<User> users = roomUsers.get(roomId);

        if (users != null) {

            users.remove(user);

            // CLEAN EMPTY ROOM
            if (users.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
    }

    public List<OnlineUserResponse> getUsers(UUID roomId) {

        return roomUsers
                .getOrDefault(roomId, Collections.emptySet())
                .stream()
                .map(user ->
                        OnlineUserResponse.builder()
                                .id(user.getId().toString())
                                .username(user.getUsername())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .avatar(user.getAvatar())
                                .build()
                )
                .toList();
    }

    public Set<User> getRoomUsers(UUID roomId) {

        return roomUsers.getOrDefault(
                roomId,
                Collections.emptySet()
        );
    }
}