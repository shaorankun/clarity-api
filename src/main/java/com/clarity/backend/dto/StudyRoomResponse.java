package com.clarity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudyRoomResponse {

    private UUID id;

    private UUID ownerId;

    private String name;

    private String inviteCode;

    private boolean isActive;

    private List<RoomMemberResponse> roomMembers;
}
