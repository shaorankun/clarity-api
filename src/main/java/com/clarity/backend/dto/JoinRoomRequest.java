package com.clarity.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JoinRoomRequest {

    private String inviteCode;

}
