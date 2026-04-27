package com.clarity.backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomSessionRequest {

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

}
