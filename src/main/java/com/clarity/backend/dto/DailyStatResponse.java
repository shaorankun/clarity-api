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
public class DailyStatResponse {

    private LocalDate date;

    private int totalMinutes;

    private int sessionCount;

    private int tasksCompleted;
}
