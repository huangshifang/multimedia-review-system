package com.multimediareview.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CompetitionResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer maxRank;
    private Integer totalParticipants;
    private Integer scoreModifyWindowMinutes;
    private String createdByName;
    private Long participantCount;
    private Long judgeCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private List<RankConfigResponse> rankConfigs;

    public CompetitionResponse() {}

    public CompetitionResponse(Long id, String name, String description, String status,
                               Integer maxRank, Integer totalParticipants,
                               Integer scoreModifyWindowMinutes,
                               String createdByName, Long participantCount, Long judgeCount,
                               LocalDateTime startTime, LocalDateTime endTime,
                               LocalDateTime createdAt, List<RankConfigResponse> rankConfigs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.maxRank = maxRank;
        this.totalParticipants = totalParticipants;
        this.scoreModifyWindowMinutes = scoreModifyWindowMinutes;
        this.createdByName = createdByName;
        this.participantCount = participantCount;
        this.judgeCount = judgeCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.rankConfigs = rankConfigs;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getMaxRank() { return maxRank; }
    public void setMaxRank(Integer maxRank) { this.maxRank = maxRank; }
    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }
    public Integer getScoreModifyWindowMinutes() { return scoreModifyWindowMinutes; }
    public void setScoreModifyWindowMinutes(Integer scoreModifyWindowMinutes) { this.scoreModifyWindowMinutes = scoreModifyWindowMinutes; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public Long getParticipantCount() { return participantCount; }
    public void setParticipantCount(Long participantCount) { this.participantCount = participantCount; }
    public Long getJudgeCount() { return judgeCount; }
    public void setJudgeCount(Long judgeCount) { this.judgeCount = judgeCount; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<RankConfigResponse> getRankConfigs() { return rankConfigs; }
    public void setRankConfigs(List<RankConfigResponse> rankConfigs) { this.rankConfigs = rankConfigs; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionResponse that = (CompetitionResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }

    @Override
    public String toString() {
        return "CompetitionResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", maxRank=" + maxRank +
                ", totalParticipants=" + totalParticipants +
                ", participantCount=" + participantCount +
                ", judgeCount=" + judgeCount +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String status;
        private Integer maxRank;
        private Integer totalParticipants;
        private Integer scoreModifyWindowMinutes;
        private String createdByName;
        private Long participantCount;
        private Long judgeCount;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private List<RankConfigResponse> rankConfigs;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder maxRank(Integer maxRank) { this.maxRank = maxRank; return this; }
        public Builder totalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; return this; }
        public Builder scoreModifyWindowMinutes(Integer scoreModifyWindowMinutes) { this.scoreModifyWindowMinutes = scoreModifyWindowMinutes; return this; }
        public Builder createdByName(String createdByName) { this.createdByName = createdByName; return this; }
        public Builder participantCount(Long participantCount) { this.participantCount = participantCount; return this; }
        public Builder judgeCount(Long judgeCount) { this.judgeCount = judgeCount; return this; }
        public Builder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public Builder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder rankConfigs(List<RankConfigResponse> rankConfigs) { this.rankConfigs = rankConfigs; return this; }

        public CompetitionResponse build() {
            return new CompetitionResponse(id, name, description, status, maxRank,
                    totalParticipants, scoreModifyWindowMinutes, createdByName,
                    participantCount, judgeCount, startTime, endTime, createdAt, rankConfigs);
        }
    }

    public static class RankConfigResponse {
        private Long id;
        private Integer rankNumber;
        private Integer capacity;

        public RankConfigResponse() {}

        public RankConfigResponse(Long id, Integer rankNumber, Integer capacity) {
            this.id = id;
            this.rankNumber = rankNumber;
            this.capacity = capacity;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getRankNumber() { return rankNumber; }
        public void setRankNumber(Integer rankNumber) { this.rankNumber = rankNumber; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RankConfigResponse that = (RankConfigResponse) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(rankNumber, that.rankNumber) &&
                    Objects.equals(capacity, that.capacity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, rankNumber, capacity);
        }

        @Override
        public String toString() {
            return "RankConfigResponse{" +
                    "id=" + id +
                    ", rankNumber=" + rankNumber +
                    ", capacity=" + capacity +
                    '}';
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long id;
            private Integer rankNumber;
            private Integer capacity;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder rankNumber(Integer rankNumber) { this.rankNumber = rankNumber; return this; }
            public Builder capacity(Integer capacity) { this.capacity = capacity; return this; }

            public RankConfigResponse build() {
                return new RankConfigResponse(id, rankNumber, capacity);
            }
        }
    }
}
