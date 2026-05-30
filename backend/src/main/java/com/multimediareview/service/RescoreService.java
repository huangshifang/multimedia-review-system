package com.multimediareview.service;

import com.multimediareview.dto.RescoreRequest;
import com.multimediareview.dto.ScoreSubmitRequest;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.ScoreStatus;
import com.multimediareview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RescoreService {

    private final RescoreRoundRepository rescoreRoundRepository;
    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;

    public RescoreService(RescoreRoundRepository rescoreRoundRepository,
                          ScoreRepository scoreRepository,
                          CompetitionRepository competitionRepository,
                          CompetitionParticipantRepository participantRepository) {
        this.rescoreRoundRepository = rescoreRoundRepository;
        this.scoreRepository = scoreRepository;
        this.competitionRepository = competitionRepository;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public RescoreRound initiateRescore(Long competitionId, RescoreRequest request) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        long count = rescoreRoundRepository.countByCompetitionId(competitionId);
        RescoreRound round = RescoreRound.builder()
                .competition(competition)
                .roundNumber((int) (count + 1))
                .reason(request.getReason())
                .build();
        return rescoreRoundRepository.save(round);
    }

    @Transactional
    public void submitRescoreScores(Long competitionId, Long roundId, Long judgeId,
                                     ScoreSubmitRequest request) {
        RescoreRound round = rescoreRoundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("复评轮次不存在"));

        User judge = new User();
        judge.setId(judgeId);

        for (var item : request.getScores()) {
            CompetitionParticipant participant = participantRepository
                    .findById(item.getParticipantId())
                    .orElseThrow(() -> new RuntimeException("参评人不存在"));

            Score score = Score.builder()
                    .competition(round.getCompetition())
                    .judge(judge)
                    .participant(participant)
                    .score(item.getScore())
                    .status(ScoreStatus.SUBMITTED)
                    .rescoreRound(round)
                    .build();
            scoreRepository.save(score);
        }
    }

    public List<RescoreRound> getRounds(Long competitionId) {
        return rescoreRoundRepository.findByCompetitionIdOrderByRoundNumberAsc(competitionId);
    }
}
