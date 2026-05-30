package com.multimediareview.controller;

import com.multimediareview.dto.JudgeAssignmentRequest;
import com.multimediareview.dto.JudgeResponse;
import com.multimediareview.service.JudgeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}/judges")
public class JudgeController {

    private final JudgeService judgeService;

    public JudgeController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    @PostMapping
    public ResponseEntity<List<JudgeResponse>> assign(@PathVariable Long competitionId,
                                                       @Valid @RequestBody JudgeAssignmentRequest request) {
        return ResponseEntity.ok(judgeService.assignJudges(competitionId, request.getUserIds()));
    }

    @GetMapping
    public ResponseEntity<List<JudgeResponse>> list(@PathVariable Long competitionId) {
        return ResponseEntity.ok(judgeService.listJudges(competitionId));
    }

    @DeleteMapping("/{judgeId}")
    public ResponseEntity<Void> remove(@PathVariable Long competitionId,
                                        @PathVariable Long judgeId) {
        judgeService.removeJudge(competitionId, judgeId);
        return ResponseEntity.noContent().build();
    }
}
