package com.sports.core.strategy;

import com.sports.core.competition.Leaderboard;
import com.sports.core.competition.Standings;
import java.util.List;

/**
 * Combines the leaderboards of several child {@link Standings} (the events of a
 * championship) into one overall table — e.g. an Olympic medal table or a
 * total-points ranking. The competition-tier counterpart of
 * {@link StandingsAggregator}; chosen per championship, never hard-wired.
 */
public interface OverallAggregator {
    Leaderboard aggregate(List<Standings> children);
}
