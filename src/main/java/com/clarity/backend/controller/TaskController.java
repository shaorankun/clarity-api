package com.clarity.backend.controller;

import com.clarity.backend.dto.TaskRequest;
import com.clarity.backend.dto.TaskResponse;
import com.clarity.backend.model.User;
import com.clarity.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.status(200).body(taskService.getTasks(getCurrentUser()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.status(201).body(taskService.createTask(getCurrentUser(), taskRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.status(200).body(taskService.updateTask(id, getCurrentUser(), taskRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id, getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> patchTask(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(taskService.completeTask(id, getCurrentUser()));
    }

    // Avoiding sent user information in request body (BOLA security exploit)
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        return (User) authentication.getPrincipal();
    }
}
