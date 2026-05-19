package com.clarity.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyRoomRequest {

    @NotBlank(message = "Room name is required")
    private String name;

    @JsonProperty("isPublic")
    private boolean isPublic;
}
