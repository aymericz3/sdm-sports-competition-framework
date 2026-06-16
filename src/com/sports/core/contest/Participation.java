package com.sports.core.contest;

import com.sports.core.entity.Participant;
import java.util.UUID;

public class Participation {
    private final String id;
    private final Participant participant;
    private final String role;
    private ParticipationStatus status;
    private String outcomeValue;

    public Participation(Participant participant, String role) {
        this.id = UUID.randomUUID().toString();
        this.participant = participant;
        this.role = role;
        this.status = ParticipationStatus.ENTERED;
    }

    public String getId()                  { return id; }
    public Participant getParticipant()    { return participant; }
    public String getRole()                { return role; }
    public ParticipationStatus getStatus() { return status; }
    public String getOutcomeValue()        { return outcomeValue; }

    public void activate() {
        if (status != ParticipationStatus.ENTERED) {
            throw new IllegalStateException("Can only activate from ENTERED");
        }
        status = ParticipationStatus.ACTIVE;
    }

    public void complete(String value) {
        if (status != ParticipationStatus.ACTIVE) {
            throw new IllegalStateException("Can only complete from ACTIVE");
        }
        outcomeValue = value;
        status = ParticipationStatus.COMPLETED;
    }

    public void didNotFinish() {
        if (status != ParticipationStatus.ACTIVE) {
            throw new IllegalStateException("Can only mark DID_NOT_FINISH from ACTIVE");
        }
        status = ParticipationStatus.DID_NOT_FINISH;
    }

    public void didNotStart() {
        if (status != ParticipationStatus.ENTERED &&
            status != ParticipationStatus.ACTIVE) {
            throw new IllegalStateException("Can only mark DID_NOT_START from ENTERED or ACTIVE");
        }
        status = ParticipationStatus.DID_NOT_START;
    }

    public void disqualify() {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot disqualify a terminal participation");
        }
        status = ParticipationStatus.DISQUALIFIED;
    }

    public void withdraw() {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot withdraw a terminal participation");
        }
        status = ParticipationStatus.WITHDREW;
    }

    @Override
    public String toString() {
        return participant.getName() + (role != null ? "[" + role + "]" : "");
    }
}