package com.multimediareview.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

public class JudgeAssignmentRequest {
    @NotEmpty
    private List<Long> userIds;

    // Getters and Setters
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JudgeAssignmentRequest that = (JudgeAssignmentRequest) o;
        return Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIds);
    }

    @Override
    public String toString() {
        return "JudgeAssignmentRequest{" +
                "userIds=" + userIds +
                '}';
    }
}
