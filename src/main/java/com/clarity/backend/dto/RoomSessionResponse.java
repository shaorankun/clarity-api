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
public class RoomSessionResponse {

    private UUID roomId;

    private String status;

    private LocalDateTime startedAt;

    private Integer durationMinutes;

}
