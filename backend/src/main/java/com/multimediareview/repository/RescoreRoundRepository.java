package com.multimediareview.repository;

import com.multimediareview.entity.RescoreRound;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RescoreRoundRepository extends JpaRepository<RescoreRound, Long> {
    List<RescoreRound> findByCompetitionIdOrderByRoundNumberAsc(Long competitionId);
    long countByCompetitionId(Long competitionId);
}
