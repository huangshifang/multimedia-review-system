package com.multimediareview.repository;

import com.multimediareview.entity.Competition;
import com.multimediareview.entity.enums.CompetitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByStatusOrderByCreatedAtDesc(CompetitionStatus status);
}
