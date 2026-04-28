package com.clarity.backend.service;

import com.clarity.backend.dto.*;
import com.clarity.backend.model.User;
import com.clarity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    // Use @RequiredArgsConstructor to inject all fields with final
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisRoomService redisRoomService;

    // Return response when register
    public AuthResponse register(RegisterRequest registerRequest) {

        // Check if email has been used
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Create a new user with hash password
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setDisplayName(registerRequest.getDisplayName());

        // Save user to database
        userRepository.save(user);

        // Create token for response
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Call constructor then return
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }

    // Return response when login
    public AuthResponse login(LoginRequest loginRequest) {

        // Check if email was found in database
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // Check for password in hash
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // Create token
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Return response
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }

    // Blacklist refresh token when logout
    public void logout(LogoutRequest logoutRequest) {
        redisRoomService.blacklistToken(logoutRequest.getRefreshToken());
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isTokenValid(refreshToken, email)){
            throw new RuntimeException("Invalid refresh token");
        }

        if (redisRoomService.isBlacklisted(refreshToken)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        String accessToken = jwtService.generateAccessToken(email);
        return new RefreshTokenResponse(accessToken);
    }
}
