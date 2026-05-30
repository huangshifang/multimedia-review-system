package com.multimediareview.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Objects;

public class CompetitionCreateRequest {
    @NotBlank
    private String name;
    private String description;

    @Min(1) @Max(10)
    private Integer maxRank;

    @Min(1)
    private Integer totalParticipants;

    @Min(1) @Max(60)
    private Integer scoreModifyWindowMinutes;

    @NotEmpty
    private List<RankConfigItem> rankConfigs;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getMaxRank() { return maxRank; }
    public void setMaxRank(Integer maxRank) { this.maxRank = maxRank; }
    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }
    public Integer getScoreModifyWindowMinutes() { return scoreModifyWindowMinutes; }
    public void setScoreModifyWindowMinutes(Integer scoreModifyWindowMinutes) { this.scoreModifyWindowMinutes = scoreModifyWindowMinutes; }
    public List<RankConfigItem> getRankConfigs() { return rankConfigs; }
    public void setRankConfigs(List<RankConfigItem> rankConfigs) { this.rankConfigs = rankConfigs; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionCreateRequest that = (CompetitionCreateRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(maxRank, that.maxRank) &&
                Objects.equals(totalParticipants, that.totalParticipants) &&
                Objects.equals(scoreModifyWindowMinutes, that.scoreModifyWindowMinutes) &&
                Objects.equals(rankConfigs, that.rankConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, maxRank, totalParticipants, scoreModifyWindowMinutes, rankConfigs);
    }

    @Override
    public String toString() {
        return "CompetitionCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxRank=" + maxRank +
                ", totalParticipants=" + totalParticipants +
                ", scoreModifyWindowMinutes=" + scoreModifyWindowMinutes +
                ", rankConfigs=" + rankConfigs +
                '}';
    }

    public static class RankConfigItem {
        @Min(1) @Max(10)
        private Integer rankNumber;
        @Min(1)
        private Integer capacity;

        // Getters and Setters
        public Integer getRankNumber() { return rankNumber; }
        public void setRankNumber(Integer rankNumber) { this.rankNumber = rankNumber; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RankConfigItem that = (RankConfigItem) o;
            return Objects.equals(rankNumber, that.rankNumber) &&
                    Objects.equals(capacity, that.capacity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rankNumber, capacity);
        }

        @Override
        public String toString() {
            return "RankConfigItem{" +
                    "rankNumber=" + rankNumber +
                    ", capacity=" + capacity +
                    '}';
        }
    }
}
