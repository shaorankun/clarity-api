package com.clarity.backend.controller;

import com.clarity.backend.dto.JoinRoomRequest;
import com.clarity.backend.dto.RoomSessionResponse;
import com.clarity.backend.dto.StudyRoomRequest;
import com.clarity.backend.dto.StudyRoomResponse;
import com.clarity.backend.security.SecurityUtils;
import com.clarity.backend.service.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomResponse> getStudyRoom(@PathVariable UUID id) {
        return ResponseEntity.ok(studyRoomService.getStudyRoom(id));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<RoomSessionResponse> getStudyRoomState(@PathVariable UUID id) {
        return ResponseEntity.ok(studyRoomService.getStudyRoomState(id));
    }

    @PostMapping
    public ResponseEntity<StudyRoomResponse> createStudyRoom(@Valid @RequestBody StudyRoomRequest studyRoomRequest) {
        return ResponseEntity.status(201).body(studyRoomService.createStudyRoom(securityUtils.getCurrentUser(), studyRoomRequest));
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinStudyRoom(@RequestBody JoinRoomRequest joinRoomRequest) {
        studyRoomService.joinStudyRoom(securityUtils.getCurrentUser(), joinRoomRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveStudyRoom(@PathVariable UUID id) {
        studyRoomService.leaveStudyRoom(id, securityUtils.getCurrentUser());
        return ResponseEntity.ok().build();
    }
}
