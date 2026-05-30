package com.multimediareview.dto;

import java.util.Objects;

public class JudgeResponse {
    private Long id;
    private Long userId;
    private String username;
    private String name;

    public JudgeResponse() {}

    public JudgeResponse(Long id, Long userId, String username, String name) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JudgeResponse that = (JudgeResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, username, name);
    }

    @Override
    public String toString() {
        return "JudgeResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String username;
        private String name;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder name(String name) { this.name = name; return this; }

        public JudgeResponse build() {
            return new JudgeResponse(id, userId, username, name);
        }
    }
}
