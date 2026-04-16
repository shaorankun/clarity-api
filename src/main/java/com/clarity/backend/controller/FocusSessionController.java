package com.clarity.backend.controller;

import com.clarity.backend.dto.EndSessionRequest;
import com.clarity.backend.dto.SessionResponse;
import com.clarity.backend.dto.StartSessionRequest;
import com.clarity.backend.model.FocusSession;
import com.clarity.backend.security.SecurityUtils;
import com.clarity.backend.service.FocusSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionService focusSessionService;
    private final SecurityUtils securityUtils;

    @PostMapping("/start")
    public ResponseEntity<SessionResponse> startSession(@RequestBody @Valid StartSessionRequest startSessionRequest) {
        return ResponseEntity.status(201).body(focusSessionService.createSession(securityUtils.getCurrentUser(), startSessionRequest));
    }

    @PostMapping("/end")
    public ResponseEntity<SessionResponse> endSession(@RequestBody @Valid EndSessionRequest endSessionRequest) {
        return ResponseEntity.ok().body(focusSessionService.endSession(securityUtils.getCurrentUser(), endSessionRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<List<SessionResponse>> getSessionHistory() {
        return ResponseEntity.ok().body(focusSessionService.getFocusSessions(securityUtils.getCurrentUser()));
    }
}
