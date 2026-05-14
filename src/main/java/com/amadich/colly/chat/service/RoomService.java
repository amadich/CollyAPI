package com.amadich.colly.chat.service;

import com.amadich.colly.chat.dto.RoomRequest;
import com.amadich.colly.chat.dto.RoomResponse;
import com.amadich.colly.chat.dto.RoomUserResponse;
import com.amadich.colly.chat.entity.Room;
import com.amadich.colly.chat.entity.RoomMember;
import com.amadich.colly.chat.enums.RoomRole;
import com.amadich.colly.chat.repository.RoomMemberRepository;
import com.amadich.colly.chat.repository.RoomRepository;
import com.amadich.colly.user.entity.User;
import com.amadich.colly.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    private final RoomMemberRepository roomMemberRepository;

    private final UserRepository userRepository;

    private final RoomPresenceService roomPresenceService;

    // =========================
    // CREATE ROOM
    // =========================
    public RoomResponse createRoom(
            RoomRequest request,
            Authentication authentication
    ) {

        User owner = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Room room = Room.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isPrivate(request.getIsPrivate())
                .maxMembers(request.getMaxMembers())
                .owner(owner)
                .build();

        Room savedRoom = roomRepository.save(room);

        // OWNER MEMBER
        RoomMember ownerMember = RoomMember.builder()
                .room(savedRoom)
                .user(owner)
                .role(RoomRole.OWNER)
                .joinedAt(LocalDateTime.now())
                .build();

        roomMemberRepository.save(ownerMember);

        // INVITED MEMBERS
        if (request.getMemberIds() != null
                && !request.getMemberIds().isEmpty()) {

            List<User> users =
                    userRepository.findAllById(request.getMemberIds());

            List<RoomMember> members = users.stream()
                    .filter(user ->
                            !user.getId().equals(owner.getId())
                    )
                    .map(user ->
                            RoomMember.builder()
                                    .room(savedRoom)
                                    .user(user)
                                    .role(RoomRole.MEMBER)
                                    .joinedAt(LocalDateTime.now())
                                    .build()
                    )
                    .toList();

            roomMemberRepository.saveAll(members);
        }

        return mapRoomResponse(savedRoom);
    }

    // =========================
    // GET ROOMS
    // =========================
    public List<RoomResponse> getRooms(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                .filter(room -> {

                    // PUBLIC ROOM
                    if (!room.isPrivate()) {
                        return true;
                    }

                    // OWNER
                    if (room.getOwner()
                            .getId()
                            .equals(currentUser.getId())) {

                        return true;
                    }

                    // MEMBER
                    return roomMemberRepository
                            .existsByRoomIdAndUserId(
                                    room.getId(),
                                    currentUser.getId()
                            );
                })
                .map(room -> {

                    List<RoomUserResponse> users =
                            roomPresenceService
                                    .getRoomUsers(room.getId())
                                    .stream()
                                    .map(user ->
                                            RoomUserResponse.builder()
                                                    .id(user.getId().toString())
                                                    .username(user.getUsername())
                                                    .firstName(user.getFirstName())
                                                    .lastName(user.getLastName())
                                                    .avatar(user.getAvatar())
                                                    .build()
                                    )
                                    .toList();

                    boolean isOwner =
                            room.getOwner()
                                    .getId()
                                    .equals(currentUser.getId());

                    return RoomResponse.builder()
                            .id(room.getId())
                            .name(room.getName())
                            .description(room.getDescription())
                            .isPrivate(room.isPrivate())
                            .maxMembers(room.getMaxMembers())
                            .onlineCount(users.size())
                            .isOwner(isOwner)
                            .users(users)
                            .createdAt(room.getCreatedAt())
                            .build();
                })
                .toList();
    }

    // =========================
    // UPDATE ROOM
    // =========================
    public RoomResponse updateRoom(
            UUID roomId,
            RoomRequest request,
            Authentication authentication
    ) {

        Room room = getOwnedRoom(
                roomId,
                authentication
        );

        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setPrivate(request.getIsPrivate());
        room.setMaxMembers(request.getMaxMembers());

        Room updatedRoom = roomRepository.save(room);

        // REMOVE OLD MEMBERS
        List<RoomMember> oldMembers =
                roomMemberRepository.findByRoomId(roomId);

        List<RoomMember> removableMembers =
                oldMembers.stream()
                        .filter(member ->
                                member.getRole() != RoomRole.OWNER
                        )
                        .toList();

        roomMemberRepository.deleteAll(removableMembers);

        // ADD NEW MEMBERS
        if (request.getMemberIds() != null
                && !request.getMemberIds().isEmpty()) {

            List<User> users =
                    userRepository.findAllById(request.getMemberIds());

            List<RoomMember> newMembers = users.stream()
                    .filter(user ->
                            !user.getId().equals(
                                    room.getOwner().getId()
                            )
                    )
                    .map(user ->
                            RoomMember.builder()
                                    .room(updatedRoom)
                                    .user(user)
                                    .role(RoomRole.MEMBER)
                                    .joinedAt(LocalDateTime.now())
                                    .build()
                    )
                    .toList();

            roomMemberRepository.saveAll(newMembers);
        }

        return mapRoomResponse(updatedRoom);
    }

    // =========================
    // DELETE ROOM
    // =========================
    public void deleteRoom(
            UUID roomId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        Room room =
                roomRepository
                        .findById(roomId)
                        .orElseThrow();

        // ONLY OWNER CAN DELETE
        if (!room.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            throw new RuntimeException(
                    "Only owner can delete room"
            );
        }

        roomRepository.delete(room);
    }

    // =========================
    // OWNER CHECK
    // =========================
    private Room getOwnedRoom(
            UUID roomId,
            Authentication authentication
    ) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        Room room = roomRepository.findById(roomId)
                .orElseThrow();

        if (!room.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        return room;
    }

    // =========================
    // ROOM RESPONSE MAPPER
    // =========================
    private RoomResponse mapRoomResponse(
            Room room
    ) {

        List<RoomUserResponse> users =
                roomPresenceService
                        .getRoomUsers(room.getId())
                        .stream()
                        .map(user ->
                                RoomUserResponse.builder()
                                        .id(user.getId().toString())
                                        .username(user.getUsername())
                                        .firstName(user.getFirstName())
                                        .lastName(user.getLastName())
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
                .isOwner(true)
                .users(users)
                .createdAt(room.getCreatedAt())
                .build();
    }
}