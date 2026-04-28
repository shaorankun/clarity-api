package com.clarity.backend.service;

import com.clarity.backend.dto.RoomSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisRoomService {

    private final RedisTemplate<String, String> redisTemplate;

    // Private method to get key
    private String getKey(UUID roomId) {
        return "room:" + roomId;
    }

    // Save state whenever a user start/break/end a session
    public void saveRoomState(RoomSessionResponse roomState) {
        // Create a hash operation to put(save) data
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        String key = getKey(roomState.getRoomId());

        ops.put(key, "roomId", roomState.getRoomId().toString());
        ops.put(key, "status", roomState.getStatus());
        ops.put(key, "startedAt", roomState.getStartedAt() != null
                ? roomState.getStartedAt().toString() : "");
        ops.put(key, "durationMinutes", roomState.getDurationMinutes() != null
                ? roomState.getDurationMinutes().toString() : "");
    }

    public RoomSessionResponse getRoomState(UUID roomId) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        String key = getKey(roomId);

        String status = ops.get(key, "status");
        String startedAtStr = ops.get(key, "startedAt");
        String durationMinutesStr = ops.get(key, "durationMinutes");

        // Parse data type to return
        LocalDateTime startedAt = (startedAtStr != null && !startedAtStr.isEmpty())
                ? LocalDateTime.parse(startedAtStr) : null;
        Integer durationMinutes = (durationMinutesStr != null && !durationMinutesStr.isEmpty())
                ? Integer.parseInt(durationMinutesStr) : null;

        return new RoomSessionResponse(
                roomId,
                status,
                startedAt,
                durationMinutes
        );
    }

    public void deleteRoomState(UUID roomId) {
        String key = getKey(roomId);

        redisTemplate.delete(key);
    }
}
