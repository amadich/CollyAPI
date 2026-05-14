package com.amadich.colly.chat.repository;

import com.amadich.colly.chat.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    List<RoomMember> findByUserId(UUID userId);

    List<RoomMember> findByRoomId(UUID roomId);

    Optional<RoomMember> findByRoomIdAndUserId(
            UUID roomId,
            UUID userId
    );

    boolean existsByRoomIdAndUserId(
            UUID roomId,
            UUID userId
    );

}