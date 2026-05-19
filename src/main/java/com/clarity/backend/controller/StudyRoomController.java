package com.clarity.backend.controller;

import com.clarity.backend.dto.*;
import com.clarity.backend.security.SecurityUtils;
import com.clarity.backend.service.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/public")
    public ResponseEntity<List<StudyRoomResponse>> getPublicStudyRooms() {
        return ResponseEntity.ok(studyRoomService.getPublicStudyRooms());
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
    public ResponseEntity<StudyRoomResponse> joinStudyRoom(@RequestBody JoinRoomRequest joinRoomRequest) {
        return ResponseEntity.ok(studyRoomService.joinStudyRoom(securityUtils.getCurrentUser(), joinRoomRequest));
    }

    @PostMapping("{id}/join")
    public ResponseEntity<StudyRoomResponse> joinPublicStudyRoom(@PathVariable UUID id) {
        return ResponseEntity.ok(studyRoomService.joinPublicStudyRoom(securityUtils.getCurrentUser(), id));
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveStudyRoom(@PathVariable UUID id) {
        studyRoomService.leaveStudyRoom(id, securityUtils.getCurrentUser());
        return ResponseEntity.ok().build();
    }
}
