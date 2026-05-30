package com.multimediareview.repository;

import com.multimediareview.entity.ParticipantFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipantFileRepository extends JpaRepository<ParticipantFile, Long> {
    List<ParticipantFile> findByParticipantId(Long participantId);
}
