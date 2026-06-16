package com.sports.core.contest;

import com.sports.core.entity.Participant;
import java.util.UUID;

public class Participation {
    private final String id;
    private final Participant participant;
    private final String role;
    private Contest contest;            // set once, when this participation is added to a contest
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
    public Contest getContest()            { return contest; }
    public String getRole()                { return role; }
    public ParticipationStatus getStatus() { return status; }
    public String getOutcomeValue()        { return outcomeValue; }

    /** Called by {@link Contest} when this participation is added to it. A participation belongs to exactly one contest. */
    void attachTo(Contest contest) {
        if (this.contest != null) {
            throw new IllegalStateException("Participation already belongs to a contest");
        }
        this.contest = contest;
    }

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