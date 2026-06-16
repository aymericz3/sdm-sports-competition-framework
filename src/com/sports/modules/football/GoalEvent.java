package com.sports.modules.football;

import com.sports.core.contest.Participation;

public class GoalEvent {
    private final Participation scorer;
    private final int minute;

    public GoalEvent(Participation scorer, int minute) {
        this.scorer = scorer;
        this.minute = minute;
    }

    public Participation getScorer() { return scorer; }
    public int getMinute()           { return minute; }
}
