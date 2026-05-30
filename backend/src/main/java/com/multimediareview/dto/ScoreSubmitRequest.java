package com.multimediareview.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ScoreSubmitRequest {
    @NotEmpty
    private List<ScoreItem> scores;

    // Getters and Setters
    public List<ScoreItem> getScores() { return scores; }
    public void setScores(List<ScoreItem> scores) { this.scores = scores; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreSubmitRequest that = (ScoreSubmitRequest) o;
        return Objects.equals(scores, that.scores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scores);
    }

    @Override
    public String toString() {
        return "ScoreSubmitRequest{" +
                "scores=" + scores +
                '}';
    }

    public static class ScoreItem {
        @NotNull
        private Long participantId;
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
        private BigDecimal score;

        // Getters and Setters
        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScoreItem scoreItem = (ScoreItem) o;
            return Objects.equals(participantId, scoreItem.participantId) &&
                    Objects.equals(score, scoreItem.score);
        }

        @Override
        public int hashCode() {
            return Objects.hash(participantId, score);
        }

        @Override
        public String toString() {
            return "ScoreItem{" +
                    "participantId=" + participantId +
                    ", score=" + score +
                    '}';
        }
    }
}
