package com.multimediareview.service;

import com.multimediareview.dto.ReportResponse;
import com.multimediareview.entity.*;
import com.multimediareview.entity.enums.ScoreStatus;
import com.multimediareview.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionParticipantRepository participantRepository;
    private final CompetitionRankConfigRepository rankConfigRepository;
    private final ScoreRepository scoreRepository;

    public RankingService(CompetitionRepository competitionRepository,
                          CompetitionParticipantRepository participantRepository,
                          CompetitionRankConfigRepository rankConfigRepository,
                          ScoreRepository scoreRepository) {
        this.competitionRepository = competitionRepository;
        this.participantRepository = participantRepository;
        this.rankConfigRepository = rankConfigRepository;
        this.scoreRepository = scoreRepository;
    }

    public ReportResponse generateReport(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new RuntimeException("比赛不存在"));

        List<CompetitionParticipant> participants = participantRepository
                .findByCompetitionId(competitionId);
        List<CompetitionRankConfig> rankConfigs = rankConfigRepository
                .findByCompetitionIdOrderByRankNumberAsc(competitionId);

        List<ReportResponse.ParticipantScoreDetail> details = new ArrayList<>();
        for (CompetitionParticipant p : participants) {
            List<Score> scores = scoreRepository
                    .findByCompetitionIdAndParticipantId(competitionId, p.getId())
                    .stream()
                    .filter(s -> s.getRescoreRound() == null
                            && (s.getStatus() == ScoreStatus.SUBMITTED
                                || s.getStatus() == ScoreStatus.LOCKED))
                    .collect(Collectors.toList());

            if (scores.isEmpty()) continue;

            ReportResponse.ParticipantScoreDetail detail = calculateScoreDetail(p, scores);
            details.add(detail);
        }

        details.sort((a, b) -> b.getAverageScore().compareTo(a.getAverageScore()));

        List<ReportResponse.RankResult> ranks = assignRanks(details, rankConfigs);

        return ReportResponse.builder()
                .competitionId(competition.getId())
                .competitionName(competition.getName())
                .status(competition.getStatus().name())
                .ranks(ranks)
                .build();
    }

    private ReportResponse.ParticipantScoreDetail calculateScoreDetail(
            CompetitionParticipant participant, List<Score> scores) {

        List<Score> sorted = scores.stream()
                .sorted(Comparator.comparing(Score::getScore))
                .collect(Collectors.toList());

        BigDecimal highest = sorted.get(sorted.size() - 1).getScore();
        BigDecimal lowest = sorted.get(0).getScore();

        List<BigDecimal> effectiveScores;
        BigDecimal average;
        StringBuilder process = new StringBuilder();

        if (sorted.size() >= 3) {
            process.append("原始分(").append(sorted.size()).append("个): ");
            process.append(sorted.stream().map(s -> s.getScore().toString())
                    .collect(Collectors.joining(", ")));
            process.append(" → 去掉最高分").append(highest)
                    .append("和最低分").append(lowest);

            effectiveScores = sorted.subList(1, sorted.size() - 1).stream()
                    .map(Score::getScore)
                    .collect(Collectors.toList());

            BigDecimal sum = effectiveScores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            average = sum.divide(BigDecimal.valueOf(effectiveScores.size()), 1, RoundingMode.HALF_UP);

            process.append(" → 有效分(").append(effectiveScores.size()).append("个): ")
                    .append(effectiveScores.stream().map(BigDecimal::toString)
                            .collect(Collectors.joining(", ")))
                    .append(" → 平均分 = ").append(average);
        } else {
            effectiveScores = sorted.stream().map(Score::getScore)
                    .collect(Collectors.toList());
            BigDecimal sum = effectiveScores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            average = sum.divide(BigDecimal.valueOf(effectiveScores.size()), 1, RoundingMode.HALF_UP);
            process.append("评委不足3人，不去极值 → 原始分取平均 = ").append(average);
        }

        List<ReportResponse.JudgeScoreDetail> judgeDetails = sorted.stream()
                .map(s -> ReportResponse.JudgeScoreDetail.builder()
                        .judgeId(s.getJudge().getId())
                        .judgeName(s.getJudge().getName())
                        .score(s.getScore())
                        .isHighest(sorted.size() >= 3 && s.getScore().compareTo(highest) == 0)
                        .isLowest(sorted.size() >= 3 && s.getScore().compareTo(lowest) == 0)
                        .build())
                .collect(Collectors.toList());

        return ReportResponse.ParticipantScoreDetail.builder()
                .participantId(participant.getId())
                .participantName(participant.getName())
                .department(participant.getDepartment())
                .averageScore(average)
                .judgeScores(judgeDetails)
                .removedHighest(sorted.size() >= 3 ? highest : null)
                .removedLowest(sorted.size() >= 3 ? lowest : null)
                .effectiveScores(effectiveScores)
                .calculationProcess(process.toString())
                .build();
    }

    private List<ReportResponse.RankResult> assignRanks(
            List<ReportResponse.ParticipantScoreDetail> sortedDetails,
            List<CompetitionRankConfig> rankConfigs) {

        List<ReportResponse.RankResult> results = new ArrayList<>();
        int idx = 0;
        for (CompetitionRankConfig config : rankConfigs) {
            List<ReportResponse.ParticipantScoreDetail> rankParticipants = new ArrayList<>();
            for (int i = 0; i < config.getCapacity() && idx < sortedDetails.size(); i++, idx++) {
                rankParticipants.add(sortedDetails.get(idx));
            }
            results.add(ReportResponse.RankResult.builder()
                    .rankNumber(config.getRankNumber())
                    .rankLabel("第" + config.getRankNumber() + "名")
                    .participants(rankParticipants)
                    .build());
        }
        return results;
    }

    public List<List<ReportResponse.ParticipantScoreDetail>> findTies(Long competitionId) {
        ReportResponse report = generateReport(competitionId);
        List<List<ReportResponse.ParticipantScoreDetail>> tieGroups = new ArrayList<>();

        for (ReportResponse.RankResult rank : report.getRanks()) {
            Map<BigDecimal, List<ReportResponse.ParticipantScoreDetail>> byScore = new HashMap<>();
            for (ReportResponse.ParticipantScoreDetail p : rank.getParticipants()) {
                byScore.computeIfAbsent(p.getAverageScore(), k -> new ArrayList<>()).add(p);
            }
            for (var entry : byScore.entrySet()) {
                if (entry.getValue().size() > 1) {
                    tieGroups.add(entry.getValue());
                }
            }
        }
        return tieGroups;
    }
}
