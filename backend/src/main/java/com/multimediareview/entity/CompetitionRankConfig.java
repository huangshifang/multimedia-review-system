package com.multimediareview.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "competition_rank_configs")
public class CompetitionRankConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private Integer rankNumber;

    @Column(nullable = false)
    private Integer capacity;

    public CompetitionRankConfig() {}

    public CompetitionRankConfig(Long id, Competition competition, Integer rankNumber, Integer capacity) {
        this.id = id;
        this.competition = competition;
        this.rankNumber = rankNumber;
        this.capacity = capacity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Integer getRankNumber() { return rankNumber; }
    public void setRankNumber(Integer rankNumber) { this.rankNumber = rankNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionRankConfig that = (CompetitionRankConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CompetitionRankConfig{" +
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
        private Competition competition;
        private Integer rankNumber;
        private Integer capacity;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder competition(Competition competition) { this.competition = competition; return this; }
        public Builder rankNumber(Integer rankNumber) { this.rankNumber = rankNumber; return this; }
        public Builder capacity(Integer capacity) { this.capacity = capacity; return this; }

        public CompetitionRankConfig build() {
            return new CompetitionRankConfig(id, competition, rankNumber, capacity);
        }
    }
}
