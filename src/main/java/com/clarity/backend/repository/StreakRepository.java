package com.clarity.backend.repository;

import com.clarity.backend.model.Streak;
import com.clarity.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StreakRepository extends JpaRepository<Streak, UUID> {

    Optional<Streak> findByUser(User user);
}
