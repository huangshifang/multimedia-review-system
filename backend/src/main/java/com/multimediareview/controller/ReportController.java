package com.multimediareview.controller;

import com.multimediareview.dto.ReportResponse;
import com.multimediareview.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
public class ReportController {

    private final RankingService rankingService;

    public ReportController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/report")
    public ResponseEntity<ReportResponse> report(@PathVariable Long competitionId) {
        return ResponseEntity.ok(rankingService.generateReport(competitionId));
    }

    @GetMapping("/ties")
    public ResponseEntity<List<List<ReportResponse.ParticipantScoreDetail>>> findTies(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(rankingService.findTies(competitionId));
    }
}
