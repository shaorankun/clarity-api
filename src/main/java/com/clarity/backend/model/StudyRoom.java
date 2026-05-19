package com.clarity.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User ownerUser;

    @Column(nullable = false)
    private String name;

    @Column(name = "invite_code", nullable = false)
    private String inviteCode;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
