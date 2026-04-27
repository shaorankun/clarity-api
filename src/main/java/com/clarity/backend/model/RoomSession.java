package com.clarity.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "room_id", nullable = false)
    private StudyRoom room;

    @Column(nullable = false)
    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "duration_minutes")
    // User Integer to make it nullable since it's an object
    private Integer durationMinutes;
}
