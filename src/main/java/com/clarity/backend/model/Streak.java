package com.clarity.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "streaks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OneToOne
    private User user;

    @Column(name = "current_streak")
    private int currentStreak = 0;

    @Column(name = "longest_streak")
    private int longestStreak = 0;

    @Column(name = "last_focus_date")
    private LocalDate lastFocusDate;
}
