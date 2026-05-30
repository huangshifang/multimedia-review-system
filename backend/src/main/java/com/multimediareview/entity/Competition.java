package com.multimediareview.entity;

import com.multimediareview.entity.enums.CompetitionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "competitions")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CompetitionStatus status;

    @Column(nullable = false)
    private Integer maxRank;

    @Column(name = "total_participants", nullable = false)
    private Integer totalParticipants = 0;

    @Column(nullable = false)
    private Integer scoreModifyWindowMinutes = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionRankConfig> rankConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitionJudge> judges = new ArrayList<>();

    public Competition() {}

    public Competition(Long id, String name, String description, CompetitionStatus status,
                       Integer maxRank, Integer totalParticipants,
                       Integer scoreModifyWindowMinutes, User createdBy,
                       LocalDateTime startTime, LocalDateTime endTime, LocalDateTime createdAt,
                       List<CompetitionRankConfig> rankConfigs,
                       List<CompetitionParticipant> participants,
                       List<CompetitionJudge> judges) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.maxRank = maxRank;
        this.totalParticipants = totalParticipants != null ? totalParticipants : 0;
        this.scoreModifyWindowMinutes = scoreModifyWindowMinutes != null ? scoreModifyWindowMinutes : 10;
        this.createdBy = createdBy;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.rankConfigs = rankConfigs != null ? rankConfigs : new ArrayList<>();
        this.participants = participants != null ? participants : new ArrayList<>();
        this.judges = judges != null ? judges : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = CompetitionStatus.DRAFT;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CompetitionStatus getStatus() { return status; }
    public void setStatus(CompetitionStatus status) { this.status = status; }
    public Integer getMaxRank() { return maxRank; }
    public void setMaxRank(Integer maxRank) { this.maxRank = maxRank; }
    public Integer getTotalParticipants() { return totalParticipants; }
    public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }
    public Integer getScoreModifyWindowMinutes() { return scoreModifyWindowMinutes; }
    public void setScoreModifyWindowMinutes(Integer scoreModifyWindowMinutes) { this.scoreModifyWindowMinutes = scoreModifyWindowMinutes; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<CompetitionRankConfig> getRankConfigs() { return rankConfigs; }
    public void setRankConfigs(List<CompetitionRankConfig> rankConfigs) { this.rankConfigs = rankConfigs; }
    public List<CompetitionParticipant> getParticipants() { return participants; }
    public void setParticipants(List<CompetitionParticipant> participants) { this.participants = participants; }
    public List<CompetitionJudge> getJudges() { return judges; }
    public void setJudges(List<CompetitionJudge> judges) { this.judges = judges; }

    // equals and hashCode (excluding rankConfigs, participants, judges)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString (excluding rankConfigs, participants, judges)
    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", maxRank=" + maxRank +
                ", totalParticipants=" + totalParticipants +
                ", scoreModifyWindowMinutes=" + scoreModifyWindowMinutes +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdAt=" + createdAt +
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
        private CompetitionStatus status;
        private Integer maxRank;
        private Integer totalParticipants = 0;
        private Integer scoreModifyWindowMinutes = 10;
        private User createdBy;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private List<CompetitionRankConfig> rankConfigs = new ArrayList<>();
        private List<CompetitionParticipant> participants = new ArrayList<>();
        private List<CompetitionJudge> judges = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder status(CompetitionStatus status) { this.status = status; return this; }
        public Builder maxRank(Integer maxRank) { this.maxRank = maxRank; return this; }
        public Builder totalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; return this; }
        public Builder scoreModifyWindowMinutes(Integer scoreModifyWindowMinutes) { this.scoreModifyWindowMinutes = scoreModifyWindowMinutes; return this; }
        public Builder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public Builder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public Builder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder rankConfigs(List<CompetitionRankConfig> rankConfigs) { this.rankConfigs = rankConfigs; return this; }
        public Builder participants(List<CompetitionParticipant> participants) { this.participants = participants; return this; }
        public Builder judges(List<CompetitionJudge> judges) { this.judges = judges; return this; }

        public Competition build() {
            return new Competition(id, name, description, status, maxRank,
                    totalParticipants, scoreModifyWindowMinutes, createdBy,
                    startTime, endTime, createdAt, rankConfigs, participants, judges);
        }
    }
}
