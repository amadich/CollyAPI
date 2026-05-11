package com.amadich.colly.chat.repository;

import com.amadich.colly.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
}