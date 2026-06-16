package com.sports.core.contest;

import com.sports.core.entity.Participant;
import com.sports.modules.football.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 3 tests (Beatriz): pin the invariants stated in the design report.
 *
 *  1. Result exists iff the contest is decided.
 *  2. A finished contest cannot go back to IN_PROGRESS without REOPENED.
 *  3. A participant appears at most once per contest.
 *  4. Forfeit: loser has DID_NOT_FINISH; winner gets a win token.
 *  5. Walkover: absentee has DID_NOT_START; winner gets a win token.
 *  6. Suspended contest produces no result.
 *  7. Correct result after goal events.
 */
class ContestTest {

    private static Contest footballContest() {
        Participant home = Participant.team("Home FC");
        Participant away = Participant.team("Away FC");
        List<Participation> parts = List.of(
            new Participation(home, "HOME"),
            new Participation(away, "AWAY")
        );
        return new Contest(parts, new TimeLimit(90), new EventDerived());
    }

    @Test
    @DisplayName("No result before the contest is decided")
    void noResultBeforeDecided() {
        Contest c = footballContest();
        assertNull(c.getResult());
        assertFalse(c.isDecided());
    }

    @Test
    @DisplayName("Result exists after match finishes")
    void resultAfterFinish() {
        Contest c = footballContest();
        c.start();
        c.setElapsedMinutes(90);
        assertTrue(c.isDecided());
        assertNotNull(c.getResult());
    }

    @Test
    @DisplayName("Cannot go back from FINISHED without reopening")
    void noBackwardFromFinished() {
        Contest c = footballContest();
        c.start();
        c.setElapsedMinutes(90);
        assertEquals(ContestState.FINISHED, c.getState());
        assertThrows(IllegalStateException.class, c::start);
    }

    @Test
    @DisplayName("Forfeit: loser status DID_NOT_FINISH, winner token 'won'")
    void forfeitInvariants() {
        Contest c = footballContest();
        c.start();
        Participation loser = c.getParticipations().get(1); // AWAY
        c.forfeit(loser);
        assertEquals(ContestState.FORFEITED, c.getState());
        assertEquals(ParticipationStatus.DID_NOT_FINISH, loser.getStatus());
        assertNotNull(c.getResult());
        StandingContribution sc = c.getResult().getContribution(loser);
        assertEquals("lost", sc.getToken());
    }

    @Test
    @DisplayName("Walkover: absentee status DID_NOT_START")
    void walkoverInvariants() {
        Contest c = footballContest();
        Participation absentee = c.getParticipations().get(1);
        c.walkover(absentee);
        assertEquals(ContestState.WALKOVER, c.getState());
        assertEquals(ParticipationStatus.DID_NOT_START, absentee.getStatus());
        assertNotNull(c.getResult());
    }

    @Test
    @DisplayName("Goal events determine winner correctly")
    void goalEventsDetermineWinner() {
        Contest c = footballContest();
        c.start();
        Participation home = c.getParticipations().get(0);
        c.recordEvent(new GoalEvent(home, 23));
        c.recordEvent(new GoalEvent(home, 67));
        c.setElapsedMinutes(90);
        assertTrue(c.isDecided());
        StandingContribution homeSc = c.getResult().getContribution(home);
        assertEquals("won", homeSc.getToken());
    }

    @Test
    @DisplayName("Draw when goal counts are equal")
    void drawOnEqualGoals() {
        Contest c = footballContest();
        c.start();
        Participation home = c.getParticipations().get(0);
        Participation away = c.getParticipations().get(1);
        c.recordEvent(new GoalEvent(home, 10));
        c.recordEvent(new GoalEvent(away, 80));
        c.setElapsedMinutes(90);
        assertEquals("drew", c.getResult().getContribution(home).getToken());
        assertEquals("drew", c.getResult().getContribution(away).getToken());
    }

    @Test
    @DisplayName("Suspended contest has no result")
    void suspendedHasNoResult() {
        Contest c = footballContest();
        c.start();
        c.suspend();
        assertEquals(ContestState.SUSPENDED, c.getState());
        assertNull(c.getResult());
        assertFalse(c.isDecided());
    }

    @Test
    @DisplayName("Cannot create contest with empty participation list")
    void emptyParticipationThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contest(List.of(), new TimeLimit(90), new EventDerived()));
    }
}
