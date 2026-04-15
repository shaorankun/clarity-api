package com.clarity.backend.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EndSessionRequest {

    private UUID id;

    @Pattern(regexp = "COMPLETED|ABANDONED", message = "Status must be COMPLETED or ABANDONED")
    private String status;
}
