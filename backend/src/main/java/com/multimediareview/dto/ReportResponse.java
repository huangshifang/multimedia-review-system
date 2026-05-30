package com.multimediareview.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ReportResponse {
    private Long competitionId;
    private String competitionName;
    private String status;
    private List<RankResult> ranks;

    public ReportResponse() {}

    public ReportResponse(Long competitionId, String competitionName, String status, List<RankResult> ranks) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.status = status;
        this.ranks = ranks;
    }

    // Getters and Setters
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<RankResult> getRanks() { return ranks; }
    public void setRanks(List<RankResult> ranks) { this.ranks = ranks; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportResponse that = (ReportResponse) o;
        return Objects.equals(competitionId, that.competitionId) &&
                Objects.equals(competitionName, that.competitionName) &&
                Objects.equals(status, that.status) &&
                Objects.equals(ranks, that.ranks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitionId, competitionName, status, ranks);
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "competitionId=" + competitionId +
                ", competitionName='" + competitionName + '\'' +
                ", status='" + status + '\'' +
                ", ranks=" + ranks +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long competitionId;
        private String competitionName;
        private String status;
        private List<RankResult> ranks;

        public Builder competitionId(Long competitionId) { this.competitionId = competitionId; return this; }
        public Builder competitionName(String competitionName) { this.competitionName = competitionName; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder ranks(List<RankResult> ranks) { this.ranks = ranks; return this; }

        public ReportResponse build() {
            return new ReportResponse(competitionId, competitionName, status, ranks);
        }
    }

    public static class RankResult {
        private Integer rankNumber;
        private String rankLabel;
        private List<ParticipantScoreDetail> participants;

        public RankResult() {}

        public RankResult(Integer rankNumber, String rankLabel, List<ParticipantScoreDetail> participants) {
            this.rankNumber = rankNumber;
            this.rankLabel = rankLabel;
            this.participants = participants;
        }

        // Getters and Setters
        public Integer getRankNumber() { return rankNumber; }
        public void setRankNumber(Integer rankNumber) { this.rankNumber = rankNumber; }
        public String getRankLabel() { return rankLabel; }
        public void setRankLabel(String rankLabel) { this.rankLabel = rankLabel; }
        public List<ParticipantScoreDetail> getParticipants() { return participants; }
        public void setParticipants(List<ParticipantScoreDetail> participants) { this.participants = participants; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RankResult that = (RankResult) o;
            return Objects.equals(rankNumber, that.rankNumber) &&
                    Objects.equals(rankLabel, that.rankLabel) &&
                    Objects.equals(participants, that.participants);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rankNumber, rankLabel, participants);
        }

        @Override
        public String toString() {
            return "RankResult{" +
                    "rankNumber=" + rankNumber +
                    ", rankLabel='" + rankLabel + '\'' +
                    ", participants=" + participants +
                    '}';
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer rankNumber;
            private String rankLabel;
            private List<ParticipantScoreDetail> participants;

            public Builder rankNumber(Integer rankNumber) { this.rankNumber = rankNumber; return this; }
            public Builder rankLabel(String rankLabel) { this.rankLabel = rankLabel; return this; }
            public Builder participants(List<ParticipantScoreDetail> participants) { this.participants = participants; return this; }

            public RankResult build() {
                return new RankResult(rankNumber, rankLabel, participants);
            }
        }
    }

    public static class ParticipantScoreDetail {
        private Long participantId;
        private String participantName;
        private String department;
        private BigDecimal averageScore;
        private List<JudgeScoreDetail> judgeScores;
        private BigDecimal removedHighest;
        private BigDecimal removedLowest;
        private List<BigDecimal> effectiveScores;
        private String calculationProcess;

        public ParticipantScoreDetail() {}

        public ParticipantScoreDetail(Long participantId, String participantName, String department,
                                      BigDecimal averageScore, List<JudgeScoreDetail> judgeScores,
                                      BigDecimal removedHighest, BigDecimal removedLowest,
                                      List<BigDecimal> effectiveScores, String calculationProcess) {
            this.participantId = participantId;
            this.participantName = participantName;
            this.department = department;
            this.averageScore = averageScore;
            this.judgeScores = judgeScores;
            this.removedHighest = removedHighest;
            this.removedLowest = removedLowest;
            this.effectiveScores = effectiveScores;
            this.calculationProcess = calculationProcess;
        }

        // Getters and Setters
        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
        public String getParticipantName() { return participantName; }
        public void setParticipantName(String participantName) { this.participantName = participantName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public BigDecimal getAverageScore() { return averageScore; }
        public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
        public List<JudgeScoreDetail> getJudgeScores() { return judgeScores; }
        public void setJudgeScores(List<JudgeScoreDetail> judgeScores) { this.judgeScores = judgeScores; }
        public BigDecimal getRemovedHighest() { return removedHighest; }
        public void setRemovedHighest(BigDecimal removedHighest) { this.removedHighest = removedHighest; }
        public BigDecimal getRemovedLowest() { return removedLowest; }
        public void setRemovedLowest(BigDecimal removedLowest) { this.removedLowest = removedLowest; }
        public List<BigDecimal> getEffectiveScores() { return effectiveScores; }
        public void setEffectiveScores(List<BigDecimal> effectiveScores) { this.effectiveScores = effectiveScores; }
        public String getCalculationProcess() { return calculationProcess; }
        public void setCalculationProcess(String calculationProcess) { this.calculationProcess = calculationProcess; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParticipantScoreDetail that = (ParticipantScoreDetail) o;
            return Objects.equals(participantId, that.participantId) &&
                    Objects.equals(averageScore, that.averageScore);
        }

        @Override
        public int hashCode() {
            return Objects.hash(participantId, averageScore);
        }

        @Override
        public String toString() {
            return "ParticipantScoreDetail{" +
                    "participantId=" + participantId +
                    ", participantName='" + participantName + '\'' +
                    ", averageScore=" + averageScore +
                    '}';
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long participantId;
            private String participantName;
            private String department;
            private BigDecimal averageScore;
            private List<JudgeScoreDetail> judgeScores;
            private BigDecimal removedHighest;
            private BigDecimal removedLowest;
            private List<BigDecimal> effectiveScores;
            private String calculationProcess;

            public Builder participantId(Long participantId) { this.participantId = participantId; return this; }
            public Builder participantName(String participantName) { this.participantName = participantName; return this; }
            public Builder department(String department) { this.department = department; return this; }
            public Builder averageScore(BigDecimal averageScore) { this.averageScore = averageScore; return this; }
            public Builder judgeScores(List<JudgeScoreDetail> judgeScores) { this.judgeScores = judgeScores; return this; }
            public Builder removedHighest(BigDecimal removedHighest) { this.removedHighest = removedHighest; return this; }
            public Builder removedLowest(BigDecimal removedLowest) { this.removedLowest = removedLowest; return this; }
            public Builder effectiveScores(List<BigDecimal> effectiveScores) { this.effectiveScores = effectiveScores; return this; }
            public Builder calculationProcess(String calculationProcess) { this.calculationProcess = calculationProcess; return this; }

            public ParticipantScoreDetail build() {
                return new ParticipantScoreDetail(participantId, participantName, department,
                        averageScore, judgeScores, removedHighest, removedLowest,
                        effectiveScores, calculationProcess);
            }
        }
    }

    public static class JudgeScoreDetail {
        private Long judgeId;
        private String judgeName;
        private BigDecimal score;
        private boolean isHighest;
        private boolean isLowest;

        public JudgeScoreDetail() {}

        public JudgeScoreDetail(Long judgeId, String judgeName, BigDecimal score,
                                boolean isHighest, boolean isLowest) {
            this.judgeId = judgeId;
            this.judgeName = judgeName;
            this.score = score;
            this.isHighest = isHighest;
            this.isLowest = isLowest;
        }

        // Getters and Setters
        public Long getJudgeId() { return judgeId; }
        public void setJudgeId(Long judgeId) { this.judgeId = judgeId; }
        public String getJudgeName() { return judgeName; }
        public void setJudgeName(String judgeName) { this.judgeName = judgeName; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        public boolean isHighest() { return isHighest; }
        public void setHighest(boolean highest) { isHighest = highest; }
        public boolean isLowest() { return isLowest; }
        public void setLowest(boolean lowest) { isLowest = lowest; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            JudgeScoreDetail that = (JudgeScoreDetail) o;
            return isHighest == that.isHighest &&
                    isLowest == that.isLowest &&
                    Objects.equals(judgeId, that.judgeId) &&
                    Objects.equals(score, that.score);
        }

        @Override
        public int hashCode() {
            return Objects.hash(judgeId, score, isHighest, isLowest);
        }

        @Override
        public String toString() {
            return "JudgeScoreDetail{" +
                    "judgeId=" + judgeId +
                    ", judgeName='" + judgeName + '\'' +
                    ", score=" + score +
                    ", isHighest=" + isHighest +
                    ", isLowest=" + isLowest +
                    '}';
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long judgeId;
            private String judgeName;
            private BigDecimal score;
            private boolean isHighest;
            private boolean isLowest;

            public Builder judgeId(Long judgeId) { this.judgeId = judgeId; return this; }
            public Builder judgeName(String judgeName) { this.judgeName = judgeName; return this; }
            public Builder score(BigDecimal score) { this.score = score; return this; }
            public Builder isHighest(boolean isHighest) { this.isHighest = isHighest; return this; }
            public Builder isLowest(boolean isLowest) { this.isLowest = isLowest; return this; }

            public JudgeScoreDetail build() {
                return new JudgeScoreDetail(judgeId, judgeName, score, isHighest, isLowest);
            }
        }
    }
}
