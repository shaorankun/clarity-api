package com.clarity.backend.repository;

import com.clarity.backend.model.RoomMember;
import com.clarity.backend.model.StudyRoom;
import com.clarity.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {

    List<RoomMember> findByRoom(StudyRoom studyRoom);

    boolean existsByUser(User user);

    Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId);

    List<RoomMember> findByRoomId(UUID roomId);

    Optional<RoomMember> findByUser(User user);
}
