package com.clarity.backend.service;

import com.clarity.backend.dto.RoomMemberResponse;
import com.clarity.backend.model.RoomMember;
import com.clarity.backend.model.StudyRoom;
import com.clarity.backend.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomMemberService {

    private final RoomMemberRepository roomMemberRepository;

    public List<RoomMemberResponse> getRoomMembers(StudyRoom studyRoom) {
        return roomMemberRepository.findByRoom(studyRoom)
                .stream()
                .map(this::convertRoomMemberToResponse)
                .toList();

    }

    private RoomMemberResponse convertRoomMemberToResponse(RoomMember roomMember) {
        return new RoomMemberResponse(
                roomMember.getUser().getId(),
                roomMember.getUser().getDisplayName()
        );
    }
}
