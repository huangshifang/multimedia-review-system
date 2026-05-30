package com.multimediareview.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

public class RescoreRequest {
    @NotEmpty
    private List<Long> participantIds;
    private String reason;

    // Getters and Setters
    public List<Long> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Long> participantIds) { this.participantIds = participantIds; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RescoreRequest that = (RescoreRequest) o;
        return Objects.equals(participantIds, that.participantIds) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantIds, reason);
    }

    @Override
    public String toString() {
        return "RescoreRequest{" +
                "participantIds=" + participantIds +
                ", reason='" + reason + '\'' +
                '}';
    }
}
