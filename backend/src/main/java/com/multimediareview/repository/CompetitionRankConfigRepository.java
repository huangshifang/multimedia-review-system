package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionRankConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetitionRankConfigRepository extends JpaRepository<CompetitionRankConfig, Long> {
    List<CompetitionRankConfig> findByCompetitionIdOrderByRankNumberAsc(Long competitionId);
}
