package com.sports.core.competition;

/**
 * The Composite component for the competition tier. A {@code Standings} is
 * anything that can be named and can produce a {@link Leaderboard} on demand —
 * whether it is a single {@link Competition} (a leaf) or a {@link Championship}
 * that aggregates several of them (a composite). Clients treat both uniformly.
 */
public interface Standings {
    String getName();

    Leaderboard computeStandings();
}
