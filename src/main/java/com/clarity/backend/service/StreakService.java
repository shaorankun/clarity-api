package com.clarity.backend.service;

import com.clarity.backend.model.Streak;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.StreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final StreakRepository streakRepository;

    public Streak updateStreak(User user) {

        Streak streak = streakRepository.findByUser(user).orElse(null);
        // First time done a focus session
        if (streak == null) {
            streak = new Streak();
            streak.setUser(user);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastFocusDate = streak.getLastFocusDate();

        // Reset streak or set new streak if new
        if (lastFocusDate == null  || lastFocusDate.isBefore(today.minusDays(1))) {
            streak.setCurrentStreak(1);
        }
        // Add streak
        else if (lastFocusDate.equals(today.minusDays(1))) {
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        }
        // Streak remains the same
        else {
            return streak;
        }

        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }
        streak.setLastFocusDate(LocalDate.now());
        streakRepository.save(streak);
        return streak;
    }
}
