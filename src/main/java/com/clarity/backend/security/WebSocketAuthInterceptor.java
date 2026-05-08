package com.clarity.backend.security;

import com.clarity.backend.model.User;
import com.clarity.backend.repository.UserRepository;
import com.clarity.backend.service.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    // Use Channel Interceptor to interfere with message before sending it

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // preSend check message before sending, return null to stop
    @Override
    public Message<?> preSend(@NonNull Message<?> message,@NonNull MessageChannel channel) {
        // wrap message before sending to channel and broadcast to an accessor
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Read token from header
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null) {
                throw new RuntimeException("Authorization header missing (No Token)");
            }

            if (token.startsWith("Bearer ")) token = token.substring(7);

            String extractedEmail = jwtService.extractEmail(token);
            if (extractedEmail == null) {
                throw new RuntimeException("No email found");
            }

            User user = userRepository.findByEmail(extractedEmail)
                    .orElseThrow(() -> new RuntimeException("User email not found"));

            if (!jwtService.isTokenValid(token, extractedEmail)) {
                throw new RuntimeException("Invalid token");
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of()
                    );

            accessor.setUser(authToken);
            accessor.setLeaveMutable(true);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        return message;
    }
}
