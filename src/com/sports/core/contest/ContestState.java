package com.sports.core.contest;

public enum ContestState {
    SCHEDULED, IN_PROGRESS, SUSPENDED, FINISHED,
    FORFEITED, WALKOVER, ABANDONED, CANCELLED_VOID, REOPENED;

    public boolean isDecidedState() {
        return this == FINISHED || this == FORFEITED || this == WALKOVER;
    }

    public boolean canHaveResult() {
        return this == FINISHED || this == FORFEITED || this == WALKOVER || this == ABANDONED;
    }
}
