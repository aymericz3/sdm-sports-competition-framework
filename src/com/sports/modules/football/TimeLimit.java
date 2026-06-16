package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.TerminationKind;
import com.sports.core.strategy.TerminationRule;

public class TimeLimit implements TerminationRule {
    private final int regularMinutes;

    public TimeLimit(int regularMinutes) {
        this.regularMinutes = regularMinutes;
    }

    @Override
    public TerminationKind isOver(Contest contest) {
        if (contest.getElapsedMinutes() >= regularMinutes) {
            return TerminationKind.TIME_ELAPSED;
        }

        return TerminationKind.NOT_OVER;
    }
}