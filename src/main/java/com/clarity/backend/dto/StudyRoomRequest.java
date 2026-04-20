package com.clarity.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyRoomRequest {

    @NotBlank(message = "Room name is required")
    private String name;

}
