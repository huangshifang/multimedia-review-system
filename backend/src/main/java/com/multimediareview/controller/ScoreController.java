package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.ScoreResponse;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/scores")
    public ResponseEntity<List<ScoreResponse>> submit(@PathVariable Long competitionId,
                                                       @CurrentUser JwtUserDetails userDetails,
                                                       @Valid @RequestBody ScoreSubmitRequest request) {
        return ResponseEntity.ok(scoreService.submitScores(competitionId, userDetails.getUserId(), request));
    }

    @GetMapping("/my-scores")
    public ResponseEntity<List<ScoreResponse>> myScores(@PathVariable Long competitionId,
                                                         @CurrentUser JwtUserDetails userDetails) {
        return ResponseEntity.ok(scoreService.getMyScores(competitionId, userDetails.getUserId()));
    }

    @GetMapping("/all-scores")
    public ResponseEntity<List<ScoreResponse>> allScores(@PathVariable Long competitionId) {
        return ResponseEntity.ok(scoreService.getAllScores(competitionId));
    }
}
