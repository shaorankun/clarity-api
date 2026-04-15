package com.clarity.backend.repository;

import com.clarity.backend.model.FocusSession;
import com.clarity.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FocusSessionRepository extends JpaRepository<FocusSession, UUID> {

    List<FocusSession> findByUser(User user);

    List<FocusSession> findByUserAndStartedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
