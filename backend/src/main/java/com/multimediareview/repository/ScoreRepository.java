package com.multimediareview.repository;

import com.multimediareview.entity.Score;
import com.multimediareview.entity.enums.ScoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findByCompetitionIdAndJudgeId(Long competitionId, Long judgeId);

    List<Score> findByCompetitionIdAndParticipantId(Long competitionId, Long participantId);

    List<Score> findByCompetitionIdAndRescoreRoundId(Long competitionId, Long rescoreRoundId);

    Optional<Score> findByCompetitionIdAndJudgeIdAndParticipantIdAndRescoreRoundIsNull(
            Long competitionId, Long judgeId, Long participantId);

    @Modifying
    @Transactional
    @Query("UPDATE Score s SET s.status = 'LOCKED' WHERE s.status = 'SUBMITTED' AND s.submittedAt < :cutoff")
    int lockScoresOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT s FROM Score s JOIN FETCH s.judge JOIN FETCH s.participant WHERE s.competition.id = :competitionId")
    List<Score> findAllByCompetitionId(@Param("competitionId") Long competitionId);
}
