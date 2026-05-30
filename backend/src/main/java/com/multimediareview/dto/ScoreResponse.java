package com.multimediareview.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ScoreResponse {
    private Long id;
    private Long participantId;
    private String participantName;
    private BigDecimal score;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime lockedAt;

    public ScoreResponse() {}

    public ScoreResponse(Long id, Long participantId, String participantName, BigDecimal score,
                         String status, LocalDateTime submittedAt, LocalDateTime lockedAt) {
        this.id = id;
        this.participantId = participantId;
        this.participantName = participantName;
        this.score = score;
        this.status = status;
        this.submittedAt = submittedAt;
        this.lockedAt = lockedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getParticipantName() { return participantName; }
    public void setParticipantName(String participantName) { this.participantName = participantName; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getLockedAt() { return lockedAt; }
    public void setLockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreResponse that = (ScoreResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(participantId, that.participantId) &&
                Objects.equals(participantName, that.participantName) &&
                Objects.equals(score, that.score) &&
                Objects.equals(status, that.status) &&
                Objects.equals(submittedAt, that.submittedAt) &&
                Objects.equals(lockedAt, that.lockedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, participantId, participantName, score, status, submittedAt, lockedAt);
    }

    @Override
    public String toString() {
        return "ScoreResponse{" +
                "id=" + id +
                ", participantId=" + participantId +
                ", participantName='" + participantName + '\'' +
                ", score=" + score +
                ", status='" + status + '\'' +
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
        private Long participantId;
        private String participantName;
        private BigDecimal score;
        private String status;
        private LocalDateTime submittedAt;
        private LocalDateTime lockedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder participantId(Long participantId) { this.participantId = participantId; return this; }
        public Builder participantName(String participantName) { this.participantName = participantName; return this; }
        public Builder score(BigDecimal score) { this.score = score; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder lockedAt(LocalDateTime lockedAt) { this.lockedAt = lockedAt; return this; }

        public ScoreResponse build() {
            return new ScoreResponse(id, participantId, participantName, score, status,
                    submittedAt, lockedAt);
        }
    }
}
