package com.multimediareview.service;

import com.multimediareview.dto.ScoreResponse;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.ScoreStatus;
import com.multimediareview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ScoreService(ScoreRepository scoreRepository,
                        CompetitionRepository competitionRepository,
                        CompetitionParticipantRepository participantRepository,
                        UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.competitionRepository = competitionRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<ScoreResponse> submitScores(Long competitionId, Long judgeId,
                                             ScoreSubmitRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (!competition.getStatus().name().equals("SCORING")) {
            throw new RuntimeException("比赛不在评分阶段");
        }

        User judge = userRepository.findById(judgeId)
                .orElseThrow(() -> new RuntimeException("评委不存在"));

        for (var item : request.getScores()) {
            if (item.getScore().compareTo(BigDecimal.valueOf(100)) > 0
                    || item.getScore().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("分数必须在0-100之间");
            }

            Optional<Score> existing = scoreRepository
                    .findByCompetitionIdAndJudgeIdAndParticipantIdAndRescoreRoundIsNull(
                            competitionId, judgeId, item.getParticipantId());

            Score score;
            if (existing.isPresent()) {
                score = existing.get();
                if (score.getStatus() == ScoreStatus.LOCKED) {
                    throw new RuntimeException("评分已锁定无法修改");
                }
                score.setScore(item.getScore());
                score.setStatus(ScoreStatus.SUBMITTED);
                score.setSubmittedAt(LocalDateTime.now());
            } else {
                CompetitionParticipant participant = participantRepository
                        .findById(item.getParticipantId())
                        .orElseThrow(() -> new RuntimeException("参评人不存在"));
                score = Score.builder()
                        .competition(competition)
                        .judge(judge)
                        .participant(participant)
                        .score(item.getScore())
                        .status(ScoreStatus.SUBMITTED)
                        .submittedAt(LocalDateTime.now())
                        .build();
            }
            scoreRepository.save(score);
        }

        return getMyScores(competitionId, judgeId);
    }

    public List<ScoreResponse> getMyScores(Long competitionId, Long judgeId) {
        return scoreRepository.findByCompetitionIdAndJudgeId(competitionId, judgeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ScoreResponse> getAllScores(Long competitionId) {
        return scoreRepository.findAllByCompetitionId(competitionId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ScoreResponse toResponse(Score s) {
        return ScoreResponse.builder()
                .id(s.getId())
                .participantId(s.getParticipant().getId())
                .participantName(s.getParticipant().getName())
                .score(s.getScore())
                .status(s.getStatus().name())
                .submittedAt(s.getSubmittedAt())
                .lockedAt(s.getLockedAt())
                .build();
    }
}
