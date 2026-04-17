package com.clarity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreakResponse {

    private int currentStreak;

    private int longestStreak;

    private LocalDate lastFocusDate;
}
