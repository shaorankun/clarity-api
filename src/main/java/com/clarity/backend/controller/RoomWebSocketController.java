package com.clarity.backend.controller;

import com.clarity.backend.dto.RoomSessionRequest;
import com.clarity.backend.dto.RoomSessionResponse;
import com.clarity.backend.model.User;
import com.clarity.backend.service.RoomSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomSessionService roomSessionService;

    @MessageMapping("/room/{roomId}/start")
    public void startRoom(@DestinationVariable UUID roomId,
                          RoomSessionRequest roomSessionRequest,
                          Principal principal) {
        // Use principal interface to get current user from STOMP session which was done in Interceptor
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        RoomSessionResponse roomSessionResponse = roomSessionService.startRoomSession(roomId, user, roomSessionRequest);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, roomSessionResponse);
    }

    @MessageMapping("/room/{roomId}/break")
    public void breakRoom(@DestinationVariable UUID roomId,
                          RoomSessionRequest roomSessionRequest,
                          Principal principal) {
        // Use principal interface to get current user from STOMP session which was done in Interceptor
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        RoomSessionResponse roomSessionResponse = roomSessionService.breakRoomSession(roomId, user, roomSessionRequest);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, roomSessionResponse);
    }

    @MessageMapping("/room/{roomId}/end")
    public void endRoom(@DestinationVariable UUID roomId,
                        Principal principal) {
        // Use principal interface to get current user from STOMP session which was done in Interceptor
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        RoomSessionResponse roomSessionResponse = roomSessionService.endRoomSession(roomId, user);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, roomSessionResponse);
    }
}
