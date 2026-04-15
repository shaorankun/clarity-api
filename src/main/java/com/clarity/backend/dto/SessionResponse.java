package com.clarity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private UUID id;

    private UUID userId;

    private UUID taskId;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private int durationMinutes;

    private String status;
}
