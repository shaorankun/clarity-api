package com.clarity.backend.controller;

import com.clarity.backend.dto.DailyStatResponse;
import com.clarity.backend.dto.StreakResponse;
import com.clarity.backend.security.SecurityUtils;
import com.clarity.backend.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatController {

    private final StatService statService;
    private final SecurityUtils securityUtils;

    @GetMapping("/streak")
    public ResponseEntity<StreakResponse> getStreak() {
        return ResponseEntity.ok().body(statService.getCurrentStreak(securityUtils.getCurrentUser()));
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyStatResponse> getDailyStat() {
        return ResponseEntity.ok().body(statService.getDailyStat(securityUtils.getCurrentUser()));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DailyStatResponse>> getWeeklyStat() {
        return ResponseEntity.ok().body(statService.getWeeklyStat(securityUtils.getCurrentUser()));
    }
}
