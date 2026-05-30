package com.multimediareview.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "competition_judges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"competition_id", "user_id"})
})
public class CompetitionJudge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CompetitionJudge() {}

    public CompetitionJudge(Long id, Competition competition, User user) {
        this.id = id;
        this.competition = competition;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionJudge that = (CompetitionJudge) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CompetitionJudge{" +
                "id=" + id +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Competition competition;
        private User user;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder competition(Competition competition) { this.competition = competition; return this; }
        public Builder user(User user) { this.user = user; return this; }

        public CompetitionJudge build() {
            return new CompetitionJudge(id, competition, user);
        }
    }
}
