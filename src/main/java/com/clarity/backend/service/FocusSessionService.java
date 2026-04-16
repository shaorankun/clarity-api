package com.clarity.backend.service;

import com.clarity.backend.dto.EndSessionRequest;
import com.clarity.backend.dto.SessionResponse;
import com.clarity.backend.dto.StartSessionRequest;
import com.clarity.backend.model.FocusSession;
import com.clarity.backend.model.Task;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.FocusSessionRepository;
import com.clarity.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final TaskRepository taskRepository;
    private final StreakService streakService;

    // Helper method to convert session to response object
    private SessionResponse convertSessionToResponse(FocusSession session) {
        return new SessionResponse (
                session.getId(),
                session.getUser().getId(),
                session.getTask() != null ? session.getTask().getId() : null, // Avoid NullPointer
                session.getStartedAt(),
                session.getEndedAt(),
                session.getDurationMinutes(),
                session.getStatus()
        );
    }

    // Retrieve focus sessions history
    public List<SessionResponse> getFocusSessions(User user) {
        return focusSessionRepository.findByUser(user)
                .stream()
                .map(this::convertSessionToResponse)
                .toList();
    }

    // Create a new session when start
    public SessionResponse createSession(User user, StartSessionRequest startSessionRequest) {
        FocusSession focusSession = new FocusSession();
        focusSession.setUser(user);
        Task task = null;
        if (startSessionRequest.getTaskId() != null) {
            task = taskRepository.findById(startSessionRequest.getTaskId()).orElse(null);
        }
        focusSession.setTask(task);
        focusSession.setDurationMinutes(startSessionRequest.getDurationMinutes());

        focusSessionRepository.save(focusSession);
        return convertSessionToResponse(focusSession);
    }

    // Update session when user end that session
    public SessionResponse endSession(User user, EndSessionRequest endSessionRequest) {

        FocusSession focusSession = focusSessionRepository.findById(endSessionRequest.getId()).orElseThrow(
                () -> new RuntimeException("No session found for id: " + endSessionRequest.getId())
        );

        // Not allowing modification after ending a session
        if (!focusSession.getStatus().equals("IN_PROGRESS")) {
            throw new RuntimeException("Session has already been ended");
        }

        // Need to add check for current user match the session user
        if (!focusSession.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User mismatched");
        }

        // Need to call update streak
        if (endSessionRequest.getStatus().equals("COMPLETED")) {
            streakService.updateStreak(user);
        }

        focusSession.setEndedAt(LocalDateTime.now());
        focusSession.setStatus(endSessionRequest.getStatus());
        focusSessionRepository.save(focusSession);

        return convertSessionToResponse(focusSession);
    }
}
