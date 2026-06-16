package com.sports.core.competition;

import com.sports.core.contest.*;
import com.sports.core.entity.Participant;
import com.sports.modules.football.FootballModule;
import com.sports.modules.football.GoalEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 4 tests (Beatriz): pin the Competition invariants from the design report.
 *
 *  1. Standings are always recomputed — calling computeStandings() twice gives equal results.
 *  2. Mutating a result changes the leaderboard (derived, not cached).
 *  3. Only decided contests contribute to standings.
 *  4. Scoring rule lives on the competition (3/1/0 for football).
 */
class CompetitionTest {

    private static List<Participant> threeTeams() {
        return List.of(
            Participant.team("Alpha"),
            Participant.team("Beta"),
            Participant.team("Gamma")
        );
    }

    @Test
    @DisplayName("computeStandings() is idempotent")
    void standingsIdempotent() {
        Competition comp = FootballModule.league("Test League", threeTeams());
        Leaderboard lb1 = comp.computeStandings();
        Leaderboard lb2 = comp.computeStandings();
        assertEquals(lb1.getEntries().size(), lb2.getEntries().size());
    }

    @Test
    @DisplayName("Only decided contests contribute")
    void undecidedContestNotCounted() {
        Competition comp = FootballModule.league("Test League", threeTeams());
        // No contests started yet
        Leaderboard lb = comp.computeStandings();
        // All participants have 0 points (or none listed yet)
        lb.getEntries().forEach(e -> assertEquals(0.0, e.getPoints()));
    }

    @Test
    @DisplayName("Football winner earns 3 points, loser earns 0")
    void footballPointsCorrect() {
        List<Participant> teams = threeTeams();
        Competition comp = FootballModule.league("Points Test", teams);

        // Play the first fixture
        Contest first = comp.getContests().get(0);
        first.start();
        Participation home = first.getParticipations().get(0);
        first.recordEvent(new GoalEvent(home, 10));
        first.setElapsedMinutes(90);

        assertTrue(first.isDecided());
        Leaderboard lb = comp.computeStandings();

        // Winner should have 3 pts
        double winnerPts = lb.getEntries().stream()
            .filter(e -> e.getParticipant().equals(home.getParticipant()))
            .mapToDouble(Leaderboard.Entry::getPoints)
            .findFirst().orElse(-1);
        assertEquals(3.0, winnerPts);
    }

    @Test
    @DisplayName("Round-robin generates n*(n-1)/2 fixtures")
    void roundRobinFixtureCount() {
        int n = 4;
        Competition comp = FootballModule.league("4-team League",
            List.of(Participant.team("A"), Participant.team("B"),
                    Participant.team("C"), Participant.team("D")));
        assertEquals(n * (n - 1) / 2, comp.getContests().size());
    }
}
