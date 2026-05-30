package com.multimediareview.scheduler;

import com.multimediareview.entity.Competition;
import com.multimediareview.entity.enums.CompetitionStatus;
import com.multimediareview.repository.CompetitionRepository;
import com.multimediareview.repository.ScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScoreLockScheduler {

    private static final Logger log = LoggerFactory.getLogger(ScoreLockScheduler.class);

    private final ScoreRepository scoreRepository;
    private final CompetitionRepository competitionRepository;

    public ScoreLockScheduler(ScoreRepository scoreRepository, CompetitionRepository competitionRepository) {
        this.scoreRepository = scoreRepository;
        this.competitionRepository = competitionRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void lockExpiredScores() {
        List<Competition> activeCompetitions = competitionRepository
                .findByStatusOrderByCreatedAtDesc(CompetitionStatus.SCORING);

        for (Competition c : activeCompetitions) {
            LocalDateTime cutoff = LocalDateTime.now()
                    .minusMinutes(c.getScoreModifyWindowMinutes());
            int locked = scoreRepository.lockScoresOlderThan(cutoff);
            if (locked > 0) {
                log.info("锁定比赛 [{}] 的 {} 条过期评分", c.getId(), locked);
            }
        }
    }
}
