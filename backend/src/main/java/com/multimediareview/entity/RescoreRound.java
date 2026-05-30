package com.multimediareview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rescore_rounds")
public class RescoreRound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private Integer roundNumber;

    @Column(length = 500)
    private String reason;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;

    public RescoreRound() {}

    public RescoreRound(Long id, Competition competition, Integer roundNumber, String reason,
                        LocalDateTime createdAt, LocalDateTime finishedAt) {
        this.id = id;
        this.competition = competition;
        this.roundNumber = roundNumber;
        this.reason = reason;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RescoreRound that = (RescoreRound) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RescoreRound{" +
                "id=" + id +
                ", roundNumber=" + roundNumber +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", finishedAt=" + finishedAt +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Competition competition;
        private Integer roundNumber;
        private String reason;
        private LocalDateTime createdAt;
        private LocalDateTime finishedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder competition(Competition competition) { this.competition = competition; return this; }
        public Builder roundNumber(Integer roundNumber) { this.roundNumber = roundNumber; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder finishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; return this; }

        public RescoreRound build() {
            return new RescoreRound(id, competition, roundNumber, reason, createdAt, finishedAt);
        }
    }
}
