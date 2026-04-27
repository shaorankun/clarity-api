package com.clarity.backend.repository;

import com.clarity.backend.model.RoomSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoomSessionRepository extends JpaRepository<RoomSession, UUID> {

    Optional<RoomSession> findByRoomId(UUID roomId);
}
