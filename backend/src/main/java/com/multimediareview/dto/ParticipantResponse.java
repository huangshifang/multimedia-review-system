package com.multimediareview.dto;

import java.util.List;
import java.util.Objects;

public class ParticipantResponse {
    private Long id;
    private String name;
    private String department;
    private Long userId;
    private String linkedUsername;
    private List<FileResponse> files;

    public ParticipantResponse() {}

    public ParticipantResponse(Long id, String name, String department, Long userId,
                               String linkedUsername, List<FileResponse> files) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.userId = userId;
        this.linkedUsername = linkedUsername;
        this.files = files;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getLinkedUsername() { return linkedUsername; }
    public void setLinkedUsername(String linkedUsername) { this.linkedUsername = linkedUsername; }
    public List<FileResponse> getFiles() { return files; }
    public void setFiles(List<FileResponse> files) { this.files = files; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantResponse that = (ParticipantResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(department, that.department) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(linkedUsername, that.linkedUsername) &&
                Objects.equals(files, that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, department, userId, linkedUsername, files);
    }

    @Override
    public String toString() {
        return "ParticipantResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", userId=" + userId +
                ", linkedUsername='" + linkedUsername + '\'' +
                ", files=" + files +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String department;
        private Long userId;
        private String linkedUsername;
        private List<FileResponse> files;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder linkedUsername(String linkedUsername) { this.linkedUsername = linkedUsername; return this; }
        public Builder files(List<FileResponse> files) { this.files = files; return this; }

        public ParticipantResponse build() {
            return new ParticipantResponse(id, name, department, userId, linkedUsername, files);
        }
    }

    public static class FileResponse {
        private Long id;
        private String originalName;
        private String fileType;
        private Long fileSize;
        private String downloadUrl;

        public FileResponse() {}

        public FileResponse(Long id, String originalName, String fileType, Long fileSize, String downloadUrl) {
            this.id = id;
            this.originalName = originalName;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.downloadUrl = downloadUrl;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }
        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileResponse that = (FileResponse) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(originalName, that.originalName) &&
                    Objects.equals(fileType, that.fileType) &&
                    Objects.equals(fileSize, that.fileSize) &&
                    Objects.equals(downloadUrl, that.downloadUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, originalName, fileType, fileSize, downloadUrl);
        }

        @Override
        public String toString() {
            return "FileResponse{" +
                    "id=" + id +
                    ", originalName='" + originalName + '\'' +
                    ", fileType='" + fileType + '\'' +
                    ", fileSize=" + fileSize +
                    '}';
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Long id;
            private String originalName;
            private String fileType;
            private Long fileSize;
            private String downloadUrl;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder originalName(String originalName) { this.originalName = originalName; return this; }
            public Builder fileType(String fileType) { this.fileType = fileType; return this; }
            public Builder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
            public Builder downloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; return this; }

            public FileResponse build() {
                return new FileResponse(id, originalName, fileType, fileSize, downloadUrl);
            }
        }
    }
}
