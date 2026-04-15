package com.clarity.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StartSessionRequest {

    private UUID taskId;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

}
