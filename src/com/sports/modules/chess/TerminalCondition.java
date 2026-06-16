package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.TerminationKind;
import com.sports.core.strategy.TerminationRule;
import java.util.List;

public class TerminalCondition implements TerminationRule {

    @Override
    public TerminationKind isOver(Contest contest) {

        List<Object> events = contest.getEventLog();

        if (events.isEmpty()) {
            return TerminationKind.NOT_OVER;
        }

        Object last = events.get(events.size() - 1);

        if (last instanceof MoveEvent) {
            return ((MoveEvent) last).getTerminationKind();
        }

        return TerminationKind.NOT_OVER;
    }
}