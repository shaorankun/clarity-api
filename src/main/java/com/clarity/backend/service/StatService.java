package com.clarity.backend.service;

import com.clarity.backend.dto.DailyStatResponse;
import com.clarity.backend.dto.StreakResponse;
import com.clarity.backend.model.FocusSession;
import com.clarity.backend.model.Streak;
import com.clarity.backend.model.Task;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.FocusSessionRepository;
import com.clarity.backend.repository.StreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {

    private final StreakRepository streakRepository;
    private final FocusSessionRepository focusSessionRepository;

    // Return user streak information
    public StreakResponse getCurrentStreak(User user) {
        Streak streak = streakRepository.findByUser(user).orElse(null);

        // Not calling a new streak through update service because it creates a new streak and set current to 1 (wrong in this case)
        if (streak == null) {
            streak = new Streak();
            streak.setUser(user);
            streak.setCurrentStreak(0);
            streak.setLongestStreak(0);
            streakRepository.save(streak);
        }

        return new StreakResponse(
                streak.getCurrentStreak(),
                streak.getLongestStreak(),
                streak.getLastFocusDate()
        );
    }

    // Return daily stat
    public DailyStatResponse getDailyStat(User user) {
        return dailyStat(user, LocalDate.now());
    }

    // Return weekly stat (a list with 7 daily stats)
    public List<DailyStatResponse> getWeeklyStat(User user) {
        List<DailyStatResponse> weeklyStats = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            weeklyStats.add(dailyStat(user, date));
        }

        return weeklyStats;
    }

    // Method for getting stat in 1 day
    private DailyStatResponse dailyStat(User user, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // Find all focus sessions in today
        List<FocusSession> focusSessions = focusSessionRepository.findByUserAndStartedAtBetween(user, startOfDay, endOfDay);

        // Get total minutes in a day from the previous list of sessions
        int totalMinutes = focusSessions.stream()
                .mapToInt(FocusSession::getDurationMinutes)
                .sum();
        int sessionCount = focusSessions.size();

        // Get number of completed tasks by mapping from focus sessions list with conditions
        int tasksCompleted = (int) focusSessions.stream()
                .filter(focusSession -> focusSession.getTask() != null)
                .filter(focusSession -> focusSession.getStatus().equals("COMPLETED"))
                .map(FocusSession::getTask)
                .filter(Task::isCompleted)
                .distinct()
                .count();

        return new DailyStatResponse(
                date,
                totalMinutes,
                sessionCount,
                tasksCompleted
        );
    }
}
