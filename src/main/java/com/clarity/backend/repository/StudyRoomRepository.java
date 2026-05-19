package com.clarity.backend.repository;

import com.clarity.backend.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, UUID> {

    Optional<StudyRoom> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);

    List<StudyRoom> findByIsPublicTrue();
}
