package com.multimediareview.entity;

import com.multimediareview.entity.enums.ScoreStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "scores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"competition_id", "judge_id", "participant_id", "rescore_round_id"})
})
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private User judge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private CompetitionParticipant participant;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ScoreStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescore_round_id")
    private RescoreRound rescoreRound;

    private LocalDateTime submittedAt;
    private LocalDateTime lockedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Score() {}

    public Score(Long id, Competition competition, User judge, CompetitionParticipant participant,
                 BigDecimal score, ScoreStatus status, RescoreRound rescoreRound,
                 LocalDateTime submittedAt, LocalDateTime lockedAt,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.competition = competition;
        this.judge = judge;
        this.participant = participant;
        this.score = score;
        this.status = status;
        this.rescoreRound = rescoreRound;
        this.submittedAt = submittedAt;
        this.lockedAt = lockedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ScoreStatus.DRAFT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public User getJudge() { return judge; }
    public void setJudge(User judge) { this.judge = judge; }
    public CompetitionParticipant getParticipant() { return participant; }
    public void setParticipant(CompetitionParticipant participant) { this.participant = participant; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public ScoreStatus getStatus() { return status; }
    public void setStatus(ScoreStatus status) { this.status = status; }
    public RescoreRound getRescoreRound() { return rescoreRound; }
    public void setRescoreRound(RescoreRound rescoreRound) { this.rescoreRound = rescoreRound; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score1 = (Score) o;
        return Objects.equals(id, score1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", score=" + score +
                ", status=" + status +
                ", submittedAt=" + submittedAt +
                ", lockedAt=" + lockedAt +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Competition competition;
        private User judge;
        private CompetitionParticipant participant;
        private BigDecimal score;
        private ScoreStatus status;
        private RescoreRound rescoreRound;
        private LocalDateTime submittedAt;
        private LocalDateTime lockedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder competition(Competition competition) { this.competition = competition; return this; }
        public Builder judge(User judge) { this.judge = judge; return this; }
        public Builder participant(CompetitionParticipant participant) { this.participant = participant; return this; }
        public Builder score(BigDecimal score) { this.score = score; return this; }
        public Builder status(ScoreStatus status) { this.status = status; return this; }
        public Builder rescoreRound(RescoreRound rescoreRound) { this.rescoreRound = rescoreRound; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder lockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Score build() {
            return new Score(id, competition, judge, participant, score, status, rescoreRound,
                    submittedAt, lockedAt, createdAt, updatedAt);
        }
    }
}
