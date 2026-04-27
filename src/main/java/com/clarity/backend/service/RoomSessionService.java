package com.clarity.backend.service;

import com.clarity.backend.dto.RoomSessionRequest;
import com.clarity.backend.dto.RoomSessionResponse;
import com.clarity.backend.model.RoomSession;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.RoomSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomSessionService {

    private final RoomSessionRepository roomSessionRepository;

    // Set a timer to start a new room session
    public RoomSessionResponse startRoomSession(UUID roomId, User user, RoomSessionRequest roomSessionRequest) {
        RoomSession roomSession = roomSessionRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room session not found"));

        // Validate the user is host or not
        validateRoomOwner(user, roomSession);

        roomSession.setStatus("FOCUSING");
        roomSession.setStartedAt(LocalDateTime.now());
        roomSession.setDurationMinutes(roomSessionRequest.getDurationMinutes());
        roomSessionRepository.save(roomSession);

        return convertRoomSessionToResponse(roomSession);
    }

    // Break still need duration for countdown
    public RoomSessionResponse breakRoomSession(UUID roomId, User user, RoomSessionRequest roomSessionRequest) {
        RoomSession roomSession = roomSessionRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Room session not found"));

        // Validate the user is host or not
        validateRoomOwner(user, roomSession);

        roomSession.setStatus("BREAK");
        roomSession.setStartedAt(LocalDateTime.now());
        roomSession.setDurationMinutes(roomSessionRequest.getDurationMinutes());
        roomSessionRepository.save(roomSession);

        return convertRoomSessionToResponse(roomSession);
    }

    // End session and return to idle (no timer)
    public RoomSessionResponse endRoomSession(UUID roomId, User user) {
        RoomSession roomSession = roomSessionRepository.findByRoomId(roomId)
                        .orElseThrow(() -> new RuntimeException("Room session not found"));

        // Validate the user is host or not
        validateRoomOwner(user, roomSession);

        roomSession.setStatus("IDLE");
        roomSession.setStartedAt(null);
        roomSession.setDurationMinutes(null);
        roomSessionRepository.save(roomSession);

        return convertRoomSessionToResponse(roomSession);
    }

    private RoomSessionResponse convertRoomSessionToResponse(RoomSession roomSession) {
        return new RoomSessionResponse(
                roomSession.getRoom().getId(),
                roomSession.getStatus(),
                roomSession.getStartedAt(),
                roomSession.getDurationMinutes()
        );
    }

    private void validateRoomOwner(User user, RoomSession roomSession) {
        if (!user.equals(roomSession.getRoom().getOwnerUser())) {
            throw new RuntimeException("You are not the owner of this room");
        }
    }
}
