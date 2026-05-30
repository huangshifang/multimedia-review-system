package com.multimediareview.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class ParticipantRequest {
    @NotBlank
    private String name;
    private String department;
    private Long userId;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantRequest that = (ParticipantRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(department, that.department) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, department, userId);
    }

    @Override
    public String toString() {
        return "ParticipantRequest{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", userId=" + userId +
                '}';
    }
}
