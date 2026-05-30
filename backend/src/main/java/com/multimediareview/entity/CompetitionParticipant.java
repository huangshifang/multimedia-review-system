package com.multimediareview.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "competition_participants")
public class CompetitionParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String department;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantFile> files = new ArrayList<>();

    public CompetitionParticipant() {}

    public CompetitionParticipant(Long id, Competition competition, String name,
                                  String department, Long userId, List<ParticipantFile> files) {
        this.id = id;
        this.competition = competition;
        this.name = name;
        this.department = department;
        this.userId = userId;
        this.files = files != null ? files : new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<ParticipantFile> getFiles() { return files; }
    public void setFiles(List<ParticipantFile> files) { this.files = files; }

    // equals and hashCode (excluding files)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionParticipant that = (CompetitionParticipant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString (excluding files)
    @Override
    public String toString() {
        return "CompetitionParticipant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", userId=" + userId +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Competition competition;
        private String name;
        private String department;
        private Long userId;
        private List<ParticipantFile> files = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder competition(Competition competition) { this.competition = competition; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder files(List<ParticipantFile> files) { this.files = files; return this; }

        public CompetitionParticipant build() {
            return new CompetitionParticipant(id, competition, name, department, userId, files);
        }
    }
}
