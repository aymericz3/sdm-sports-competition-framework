package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.Tiebreak;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the football tiebreak chain (report §3.3.6 and Decision Flow 3, "ranking
 * is two-stage: points first, tiebreak second"). These tests deliberately
 * construct ties and assert the chain reorders them — the behaviour the rest of
 * the suite never exercises.
 */
class TiebreakTest {

    /** Plays a full football match a-vs-b to the given scoreline and returns the decided contest. */
    private static Contest match(Participant a, Participant b, int goalsA, int goalsB) {
        Participation pa = new Participation(a, "HOME");
        Participation pb = new Participation(b, "AWAY");
        Contest c = new Contest(List.of(pa, pb), new TimeLimit(90), new EventDerived());
        c.start();
        for (int i = 0; i < goalsA; i++) c.recordEvent(new GoalEvent(pa, i + 1));
        for (int i = 0; i < goalsB; i++) c.recordEvent(new GoalEvent(pb, i + 1));
        c.setElapsedMinutes(90);
        return c;
    }

    @Test
    @DisplayName("GoalDifference breaks a tie on points (better GD ranks first)")
    void goalDifferenceBreaksPointsTie() {
        Participant a = Participant.team("A");
        Participant b = Participant.team("B");
        Participant c = Participant.team("C");

        // A and B both win one and draw one (level on points), but A's win is by more goals.
        List<Contest> decided = List.of(
            match(a, b, 0, 0),   // A v B draw
            match(a, c, 5, 0),   // A beats C by 5  -> GD +5
            match(b, c, 1, 0)    // B beats C by 1  -> GD +1
        );

        // Pass the tied pair in the "wrong" order so a no-op would leave it [B, A].
        List<Participant> ordered = new GoalDifference(null).resolve(List.of(b, a), decided);

        assertEquals(a, ordered.get(0), "A has the better goal difference and must rank first");
        assertEquals(b, ordered.get(1));
    }

    @Test
    @DisplayName("Chain forwards: HeadToHead resolves when GoalDifference is also level")
    void headToHeadFiresWhenGoalDifferenceLevel() {
        Participant a = Participant.team("A");
        Participant b = Participant.team("B");
        Participant c = Participant.team("C");

        // Engineered so A and B finish level on goal difference (both 0),
        // but A won the head-to-head meeting.
        List<Contest> decided = List.of(
            match(a, b, 2, 1),   // A beats B head-to-head; GD here A +1, B -1
            match(a, c, 0, 1),   // A loses to C;           GD here A -1  -> A total 0
            match(b, c, 1, 0)    // B beats C;              GD here B +1  -> B total 0
        );

        Tiebreak chain = new GoalDifference(new HeadToHead(null));
        List<Participant> ordered = chain.resolve(List.of(b, a), decided);

        assertEquals(a, ordered.get(0), "GD is level, so head-to-head (A beat B) decides");
        assertEquals(b, ordered.get(1));
    }

    @Test
    @DisplayName("A genuine tie is left when the chain is exhausted")
    void genuineTieLeftWhenChainExhausted() {
        Participant a = Participant.team("A");
        Participant b = Participant.team("B");

        // Identical records and no head-to-head decider: truly level.
        List<Contest> decided = List.of(match(a, b, 1, 1)); // draw

        List<Participant> ordered =
            new GoalDifference(new HeadToHead(null)).resolve(List.of(a, b), decided);

        assertEquals(2, ordered.size());
        assertTrue(ordered.contains(a) && ordered.contains(b),
            "both remain present; the chain leaves the tie rather than inventing an order");
    }
}
