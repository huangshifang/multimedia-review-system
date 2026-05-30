package com.multimediareview.repository;

import com.multimediareview.entity.CompetitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetitionParticipantRepository extends JpaRepository<CompetitionParticipant, Long> {
    List<CompetitionParticipant> findByCompetitionId(Long competitionId);
    long countByCompetitionId(Long competitionId);
    List<CompetitionParticipant> findByUserId(Long userId);
    List<CompetitionParticipant> findByCompetitionIdAndUserId(Long competitionId, Long userId);
}
