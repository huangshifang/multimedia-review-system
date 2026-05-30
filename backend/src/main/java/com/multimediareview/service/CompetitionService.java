package com.multimediareview.service;

import com.multimediareview.dto.CompetitionCreateRequest;
import com.multimediareview.dto.CompetitionResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.CompetitionStatus;
import com.multimediareview.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionRankConfigRepository rankConfigRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final CompetitionJudgeRepository judgeRepository;

    public CompetitionService(CompetitionRepository competitionRepository,
                              CompetitionRankConfigRepository rankConfigRepository,
                              CompetitionParticipantRepository participantRepository,
                              CompetitionJudgeRepository judgeRepository) {
        this.competitionRepository = competitionRepository;
        this.rankConfigRepository = rankConfigRepository;
        this.participantRepository = participantRepository;
        this.judgeRepository = judgeRepository;
    }

    @Transactional
    public CompetitionResponse create(CompetitionCreateRequest request, User creator) {
        int maxRank = request.getMaxRank();
        int totalParticipants = request.getTotalParticipants() != null ? request.getTotalParticipants() : 0;
        int nonLastCapacitySum = 0;

        // Sum non-last rank capacities; auto-calc last rank
        List<CompetitionCreateRequest.RankConfigItem> items = request.getRankConfigs();
        for (int i = 0; i < items.size(); i++) {
            var rc = items.get(i);
            if (rc.getRankNumber() != maxRank) {
                nonLastCapacitySum += rc.getCapacity() != null ? rc.getCapacity() : 0;
            }
        }

        int lastCapacity = totalParticipants - nonLastCapacitySum;
        if (lastCapacity <= 0) {
            throw new RuntimeException("总参赛人数必须大于前" + (maxRank - 1) + "名名额之和，当前剩余名额: " + lastCapacity);
        }

        Competition competition = Competition.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maxRank(maxRank)
                .totalParticipants(totalParticipants)
                .scoreModifyWindowMinutes(request.getScoreModifyWindowMinutes() != null
                        ? request.getScoreModifyWindowMinutes() : 10)
                .status(CompetitionStatus.DRAFT)
                .createdBy(creator)
                .build();

        competition = competitionRepository.save(competition);

        for (var rc : items) {
            int capacity;
            if (rc.getRankNumber() == maxRank) {
                capacity = lastCapacity;
            } else {
                capacity = rc.getCapacity();
            }
            CompetitionRankConfig config = CompetitionRankConfig.builder()
                    .competition(competition)
                    .rankNumber(rc.getRankNumber())
                    .capacity(capacity)
                    .build();
            rankConfigRepository.save(config);
        }

        return toResponse(competition);
    }

    public List<CompetitionResponse> listAll() {
        return competitionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CompetitionResponse getById(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        return toResponse(c);
    }

    @Transactional
    public CompetitionResponse startScoring(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (c.getStatus() != CompetitionStatus.DRAFT) {
            throw new RuntimeException("只有草稿状态的比赛可以开始");
        }
        c.setStatus(CompetitionStatus.SCORING);
        c.setStartTime(LocalDateTime.now());
        return toResponse(competitionRepository.save(c));
    }

    @Transactional
    public CompetitionResponse finish(Long id) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));
        if (c.getStatus() != CompetitionStatus.SCORING) {
            throw new RuntimeException("只有评分中的比赛可以结束");
        }
        c.setStatus(CompetitionStatus.FINISHED);
        c.setEndTime(LocalDateTime.now());
        return toResponse(competitionRepository.save(c));
    }

    public CompetitionResponse toResponse(Competition c) {
        List<CompetitionRankConfig> configs = rankConfigRepository
                .findByCompetitionIdOrderByRankNumberAsc(c.getId());

        return CompetitionResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .status(c.getStatus().name())
                .maxRank(c.getMaxRank())
                .totalParticipants(c.getTotalParticipants())
                .scoreModifyWindowMinutes(c.getScoreModifyWindowMinutes())
                .createdByName(c.getCreatedBy().getName())
                .participantCount(participantRepository.countByCompetitionId(c.getId()))
                .judgeCount(judgeRepository.countByCompetitionId(c.getId()))
                .startTime(c.getStartTime())
                .endTime(c.getEndTime())
                .createdAt(c.getCreatedAt())
                .rankConfigs(configs.stream()
                        .map(rc -> CompetitionResponse.RankConfigResponse.builder()
                                .id(rc.getId())
                                .rankNumber(rc.getRankNumber())
                                .capacity(rc.getCapacity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
