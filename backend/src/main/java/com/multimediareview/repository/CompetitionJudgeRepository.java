package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionJudge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CompetitionJudgeRepository extends JpaRepository<CompetitionJudge, Long> {
    List<CompetitionJudge> findByCompetitionId(Long competitionId);
    Optional<CompetitionJudge> findByCompetitionIdAndUserId(Long competitionId, Long userId);
    long countByCompetitionId(Long competitionId);
    boolean existsByCompetitionIdAndUserId(Long competitionId, Long userId);
}
