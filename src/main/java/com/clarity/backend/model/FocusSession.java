package com.clarity.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "focus_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FocusSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "task_id")
    @ManyToOne
    private Task task;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private String status = "IN_PROGRESS";

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }
}
