package com.clarity.backend.service;

import com.clarity.backend.dto.*;
import com.clarity.backend.model.RoomMember;
import com.clarity.backend.model.RoomSession;
import com.clarity.backend.model.StudyRoom;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.RoomMemberRepository;
import com.clarity.backend.repository.RoomSessionRepository;
import com.clarity.backend.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomMemberService roomMemberService;
    private final StudyRoomRepository studyRoomRepository;
    private final RoomSessionRepository roomSessionRepository;
    private final RedisRoomService redisRoomService;

    // Create a new study room
    public StudyRoomResponse createStudyRoom(User user, StudyRoomRequest studyRoomRequest) {
        // Check if this user is currently hosting or joining another room or not?
        if (roomMemberRepository.existsByUser(user)) {
            throw new RuntimeException("User already join/host a room");
        }

        StudyRoom studyRoom = new StudyRoom();
        studyRoom.setOwnerUser(user);
        studyRoom.setName(studyRoomRequest.getName());
        studyRoom.setPublic(studyRoomRequest.isPublic());
        String inviteCode;

        // Create a new generate code until it's not duplicate
        do {
            inviteCode = generateInviteCode();
        } while (studyRoomRepository.existsByInviteCode(inviteCode));

        studyRoom.setInviteCode(inviteCode);

        // Init the owner as the first room member
        RoomMember roomMember = new RoomMember();
        roomMember.setUser(user);
        roomMember.setRoom(studyRoom);

        studyRoomRepository.save(studyRoom);
        roomMemberRepository.save(roomMember);

        // Create a new room session along with this study room with default status "IDLE"
        // Save after study room to get foreign key
        RoomSession roomSession = new RoomSession();
        roomSession.setRoom(studyRoom);
        roomSession.setStatus("IDLE");
        roomSessionRepository.save(roomSession);

        // Save room state to Redis
        redisRoomService.saveRoomState(
                new RoomSessionResponse(
                        roomSession.getRoom().getId(),
                        roomSession.getStatus(),
                        roomSession.getStartedAt(),
                        roomSession.getDurationMinutes()
                )
        );

        // Get a list of current room members in that room
        List<RoomMemberResponse> roomMembers = roomMemberService.getRoomMembers(studyRoom);

        return convertStudyRoomToResponse(studyRoom, roomMembers);
    }

    // Get study room information
    public StudyRoomResponse getStudyRoom(UUID id) {
        // Check for room
        StudyRoom studyRoom = studyRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No study room found"));

        // Get a list of current room members in that room
        List<RoomMemberResponse> roomMembers = roomMemberService.getRoomMembers(studyRoom);

        return convertStudyRoomToResponse(studyRoom, roomMembers);
    }

    // Get all public study rooms information
    public List<StudyRoomResponse> getPublicStudyRooms() {
        return studyRoomRepository.findByIsPublicTrue()
                .stream()
                .map(studyRoom -> convertStudyRoomToResponse(
                        studyRoom,
                        roomMemberService.getRoomMembers(studyRoom)
                ))
                .toList();
    }

    // Get study room state (not all information like getStudyRoom)
    public RoomSessionResponse getStudyRoomState(UUID id) {
        return redisRoomService.getRoomState(id);
    }

    // Join a new study room
    public StudyRoomResponse joinStudyRoom(User user, JoinRoomRequest joinRoomRequest) {
        // Check for the room using invite code
        StudyRoom studyRoom = studyRoomRepository.findByInviteCode(joinRoomRequest.getInviteCode())
                .orElseThrow(() -> new RuntimeException("No study room found"));

        return joinRoom(user, studyRoom);
    }

    // Join a public room without invite code
    public StudyRoomResponse joinPublicStudyRoom(User user, UUID roomId) {
        // Check for the room through public ID
        StudyRoom studyRoom = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("No study room found"));

        // Check if the room is actually public
        if (!studyRoom.isPublic()) {
            throw new RuntimeException("Study room is not public");
        }

        return joinRoom(user, studyRoom);
    }

    // User leave a study room
    public void leaveStudyRoom(UUID id, User user) {
        RoomMember roomMember = roomMemberRepository.findByRoomIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("No room found / No user found"));

        // Remove user before check host
        roomMemberRepository.delete(roomMember);

        // If the user is the host then transfer host and delete room if it was the last person
        if (roomMember.getRoom().getOwnerUser().getId().equals(user.getId())) {
            transferHost(roomMember.getRoom());
        }
    }

    // Private method for handle join room logic for both private and public room
    private StudyRoomResponse joinRoom(User user, StudyRoom studyRoom) {
        // Check if user is in another room
        if (roomMemberRepository.existsByUser(user)) {
            throw new RuntimeException("You already joined a study room");
        }

        RoomMember roomMember = new RoomMember();
        roomMember.setRoom(studyRoom);
        roomMember.setUser(user);

        roomMemberRepository.save(roomMember);

        List<RoomMemberResponse> roomMembers = roomMemberService.getRoomMembers(studyRoom);

        return convertStudyRoomToResponse(studyRoom, roomMembers);
    }

    // Private method to automatically transfer host when the current host leave
    private void transferHost(StudyRoom studyRoom) {
        // Sort a room member list with joined_at column to get the earliest person joined the room after the host
        List<RoomMember> roomMembersSortedByJoinedAt = roomMemberRepository.findByRoomId(studyRoom.getId())
                                                                        .stream()
                                                                        .sorted(Comparator.comparing(RoomMember::getJoinedAt))
                                                                        .toList();

        // Delete room if last person leave
        if (roomMembersSortedByJoinedAt.isEmpty()) {
            studyRoomRepository.delete(studyRoom);
            redisRoomService.deleteRoomState(studyRoom.getId());
            return;
        }

        User host = roomMembersSortedByJoinedAt.getFirst().getUser();
        studyRoom.setOwnerUser(host);
        studyRoomRepository.save(studyRoom);
    }

    // Helper method to convert to StudyRoomResponse object
    private StudyRoomResponse convertStudyRoomToResponse(StudyRoom studyRoom, List<RoomMemberResponse> roomMembers) {
        return new StudyRoomResponse(
                studyRoom.getId(),
                studyRoom.getOwnerUser().getId(),
                studyRoom.getName(),
                studyRoom.getInviteCode(),
                studyRoom.isActive(),
                studyRoom.isPublic(),
                roomMembers
        );
    }

    // Helper method to generate a random invite code with 6 random characters
    private String generateInviteCode() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder inviteCode = new StringBuilder(length);
        Random random = new Random();


        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            inviteCode.append(characters.charAt(index));
        }

        return inviteCode.toString();
    }
}
