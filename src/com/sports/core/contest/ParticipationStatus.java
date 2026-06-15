package com.sports.core.contest;

public enum ParticipationStatus {
    ENTERED, ACTIVE, COMPLETED, DID_NOT_FINISH, DID_NOT_START, DISQUALIFIED, WITHDREW;

    public boolean isTerminal() {
        return this == COMPLETED || this == DID_NOT_FINISH || this == DID_NOT_START
            || this == DISQUALIFIED || this == WITHDREW;
    }
}
