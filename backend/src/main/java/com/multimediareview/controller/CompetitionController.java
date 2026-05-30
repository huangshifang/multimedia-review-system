package com.multimediareview.controller;

import com.multimediareview.config.CurrentUser;
import com.multimediareview.config.JwtUserDetails;
import com.multimediareview.dto.CompetitionCreateRequest;
import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.entity.User;
import com.multimediareview.repository.UserRepository;
import com.multimediareview.service.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final UserRepository userRepository;

    public CompetitionController(CompetitionService competitionService,
                                  UserRepository userRepository) {
        this.competitionService = competitionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<CompetitionResponse> create(@Valid @RequestBody CompetitionCreateRequest request,
                                                       @CurrentUser JwtUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseEntity.ok(competitionService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> list() {
        return ResponseEntity.ok(competitionService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetitionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getById(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<CompetitionResponse> startScoring(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.startScoring(id));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<CompetitionResponse> finish(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.finish(id));
    }
}
