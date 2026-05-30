package com.multimediareview.entity;

import com.multimediareview.entity.enums.FileType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "participant_files")
public class ParticipantFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private CompetitionParticipant participant;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FileType fileType;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    public ParticipantFile() {}

    public ParticipantFile(Long id, CompetitionParticipant participant, String fileName,
                           String originalName, FileType fileType, String filePath,
                           Long fileSize, String mimeType, LocalDateTime uploadedAt) {
        this.id = id;
        this.participant = participant;
        this.fileName = fileName;
        this.originalName = originalName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CompetitionParticipant getParticipant() { return participant; }
    public void setParticipant(CompetitionParticipant participant) { this.participant = participant; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantFile that = (ParticipantFile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ParticipantFile{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", fileType=" + fileType +
                ", fileSize=" + fileSize +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private CompetitionParticipant participant;
        private String fileName;
        private String originalName;
        private FileType fileType;
        private String filePath;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime uploadedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder participant(CompetitionParticipant participant) { this.participant = participant; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder originalName(String originalName) { this.originalName = originalName; return this; }
        public Builder fileType(FileType fileType) { this.fileType = fileType; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public Builder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
        public Builder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public ParticipantFile build() {
            return new ParticipantFile(id, participant, fileName, originalName, fileType,
                    filePath, fileSize, mimeType, uploadedAt);
        }
    }
}
