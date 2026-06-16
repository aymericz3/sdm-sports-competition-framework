package com.sports.core.contest;

import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.TerminationRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Contest {
    private final String id;
    private final List<Participation> participations;
    private final TerminationRule terminationRule;
    private final OutcomeRule outcomeRule;
    private final List<Object> eventLog;

    private ContestState state;
    private Result result;
    private TerminationKind terminationKind;
    private int elapsedMinutes;

    public Contest(List<Participation> participations,
                   TerminationRule terminationRule,
                   OutcomeRule outcomeRule) {
        if (participations == null || participations.isEmpty()) {
            throw new IllegalArgumentException("A contest needs at least one participation");
        }

        this.id = UUID.randomUUID().toString();
        this.participations = new ArrayList<>(participations);
        this.terminationRule = terminationRule;
        this.outcomeRule = outcomeRule;
        this.eventLog = new ArrayList<>();
        this.state = ContestState.SCHEDULED;
        this.terminationKind = TerminationKind.NOT_OVER;

        for (Participation participation : this.participations) {
            participation.attachTo(this);
        }
    }

    public String getId()                       { return id; }
    public ContestState getState()              { return state; }
    public List<Participation> getParticipations() { return Collections.unmodifiableList(participations); }
    public Result getResult()                   { return result; }
    public TerminationKind getTerminationKind() { return terminationKind; }
    public int getElapsedMinutes()              { return elapsedMinutes; }
    public List<Object> getEventLog()           { return Collections.unmodifiableList(eventLog); }
    public TerminationRule getTerminationRule() { return terminationRule; }
    public OutcomeRule getOutcomeRule()         { return outcomeRule; }

    public boolean isDecided() {
        return state == ContestState.FINISHED ||
               state == ContestState.FORFEITED ||
               state == ContestState.WALKOVER;
    }

    public void start() {
        require(state == ContestState.SCHEDULED, "Contest can only start from SCHEDULED");

        for (Participation participation : participations) {
            participation.activate();
        }

        state = ContestState.IN_PROGRESS;
    }

    public void suspend() {
        require(state == ContestState.IN_PROGRESS, "Contest can only be suspended from IN_PROGRESS");
        state = ContestState.SUSPENDED;
    }

    public void resume() {
        require(state == ContestState.SUSPENDED, "Contest can only be resumed from SUSPENDED");
        state = ContestState.IN_PROGRESS;
    }

    public void cancel() {
        require(!isDecided() && state != ContestState.CANCELLED_VOID,
                "Contest cannot be cancelled once decided or already cancelled");
        state = ContestState.CANCELLED_VOID;
        result = null;
        terminationKind = TerminationKind.NOT_OVER;
    }

    public void reopen() {
        require(state == ContestState.FINISHED, "Only FINISHED contests can be reopened");
        state = ContestState.REOPENED;
        result = null;
        terminationKind = TerminationKind.NOT_OVER;
    }

    public void resumeCorrection() {
        require(state == ContestState.REOPENED, "Correction can only resume from REOPENED");
        state = ContestState.IN_PROGRESS;
    }

    public void recordEvent(Object event) {
        require(state == ContestState.IN_PROGRESS, "Events can only be recorded while IN_PROGRESS");
        eventLog.add(event);
        checkTermination();
    }

    public void setElapsedMinutes(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Elapsed minutes cannot be negative");
        }

        elapsedMinutes = minutes;

        if (state == ContestState.IN_PROGRESS) {
            checkTermination();
        }
    }

    public void forfeit(Participation loser) {
        require(state == ContestState.SCHEDULED || state == ContestState.IN_PROGRESS,
                "Forfeit can only happen from SCHEDULED or IN_PROGRESS");
        require(participations.contains(loser), "Loser must belong to this contest");

        if (loser.getStatus() == ParticipationStatus.ENTERED) {
            loser.activate();
        }

        loser.didNotFinish();
        state = ContestState.FORFEITED;
        terminationKind = TerminationKind.FORFEIT;
        result = outcomeRule.buildResult(this);
    }

    public void walkover(Participation absentee) {
        require(state == ContestState.SCHEDULED || state == ContestState.IN_PROGRESS,
                "Walkover can only happen from SCHEDULED or IN_PROGRESS");
        require(participations.contains(absentee), "Absentee must belong to this contest");

        absentee.didNotStart();
        state = ContestState.WALKOVER;
        terminationKind = TerminationKind.WALKOVER;
        result = outcomeRule.buildResult(this);
    }

    public void abandon() {
        require(state == ContestState.IN_PROGRESS, "Contest can only be abandoned from IN_PROGRESS");
        state = ContestState.ABANDONED;
        terminationKind = TerminationKind.ABANDONED;
        result = null;
    }

    public void acceptAbandonedResult() {
        require(state == ContestState.ABANDONED, "Only ABANDONED contests can have their result accepted");
        state = ContestState.FINISHED;
        result = outcomeRule.buildResult(this);
    }

    public void checkTermination() {
        TerminationKind kind = terminationRule.isOver(this);

        if (kind != TerminationKind.NOT_OVER) {
            terminationKind = kind;
            finalise(kind);
        }
    }

    private void finalise(TerminationKind kind) {
        terminationKind = kind;
        state = ContestState.FINISHED;

        result = outcomeRule.buildResult(this);

        for (Participation participation : participations) {
            if (participation.getStatus() == ParticipationStatus.ACTIVE) {
                participation.complete(null);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    @Override
    public String toString() {
        return "Contest[" + id.substring(0, 6) + ", " + state + "]";
    }
}