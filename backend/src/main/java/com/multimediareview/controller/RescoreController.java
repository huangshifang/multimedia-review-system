package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.RescoreRequest;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.RescoreRound;
import com.multimediareview.service.RescoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}")
public class RescoreController {

    private final RescoreService rescoreService;

    public RescoreController(RescoreService rescoreService) {
        this.rescoreService = rescoreService;
    }

    @PostMapping("/rescore")
    public ResponseEntity<RescoreRound> initiate(@PathVariable Long competitionId,
                                                  @Valid @RequestBody RescoreRequest request) {
        return ResponseEntity.ok(rescoreService.initiateRescore(competitionId, request));
    }

    @PostMapping("/rescore/{roundId}/scores")
    public ResponseEntity<Void> submitScores(@PathVariable Long competitionId,
                                              @PathVariable Long roundId,
                                              @CurrentUser JwtUserDetails userDetails,
                                              @Valid @RequestBody ScoreSubmitRequest request) {
        rescoreService.submitRescoreScores(competitionId, roundId, userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rescore-rounds")
    public ResponseEntity<List<RescoreRound>> listRounds(@PathVariable Long competitionId) {
        return ResponseEntity.ok(rescoreService.getRounds(competitionId));
    }
}
