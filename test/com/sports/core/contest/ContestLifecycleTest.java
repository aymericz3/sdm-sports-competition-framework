package com.sports.core.contest;

import com.sports.core.entity.Participant;
import com.sports.modules.football.EventDerived;
import com.sports.modules.football.GoalEvent;
import com.sports.modules.football.TimeLimit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the contest state machine edges from report §3.5 that the rest of the
 * suite does not exercise: the audited reopen path, abandonment, cancellation,
 * and the participation terminal-status guards.
 */
class ContestLifecycleTest {

    private static Contest freshMatch() {
        Participation home = new Participation(Participant.team("Home"), "HOME");
        Participation away = new Participation(Participant.team("Away"), "AWAY");
        return new Contest(List.of(home, away), new TimeLimit(90), new EventDerived());
    }

    private static Contest decidedMatch() {
        Contest c = freshMatch();
        c.start();
        c.recordEvent(new GoalEvent(c.getParticipations().get(0), 10)); // home scores
        c.setElapsedMinutes(90);
        return c;
    }

    @Test
    @DisplayName("Reopen is the only path back from FINISHED, and clears the result")
    void reopenThenResumeCorrection() {
        Contest c = decidedMatch();
        assertEquals(ContestState.FINISHED, c.getState());
        assertNotNull(c.getResult());

        c.reopen();
        assertEquals(ContestState.REOPENED, c.getState());
        assertNull(c.getResult(), "reopening clears the result; standings must be recomputed");

        c.resumeCorrection();
        assertEquals(ContestState.IN_PROGRESS, c.getState());
    }

    @Test
    @DisplayName("A scheduled contest cannot be reopened")
    void reopenFromScheduledThrows() {
        assertThrows(IllegalStateException.class, () -> freshMatch().reopen());
    }

    @Test
    @DisplayName("Abandonment produces no result until the discipline accepts it")
    void abandonedHasNoResultUntilAccepted() {
        Contest c = freshMatch();
        c.start();
        c.abandon();

        assertEquals(ContestState.ABANDONED, c.getState());
        assertFalse(c.isDecided());
        assertNull(c.getResult());

        c.acceptAbandonedResult();
        assertEquals(ContestState.FINISHED, c.getState());
        assertTrue(c.isDecided());
        assertNotNull(c.getResult(), "an abandoned contest that stands carries a result");
    }

    @Test
    @DisplayName("A cancelled/void contest is never decided and carries no result")
    void cancelledVoidHasNoResult() {
        Contest c = freshMatch();
        c.cancel();

        assertEquals(ContestState.CANCELLED_VOID, c.getState());
        assertFalse(c.isDecided());
        assertNull(c.getResult());
    }

    @Test
    @DisplayName("A participation cannot reach a second terminal status")
    void terminalStatusIsFinal() {
        Participation p = new Participation(Participant.individual("Solo"), null);
        p.withdraw();
        assertEquals(ParticipationStatus.WITHDREW, p.getStatus());
        assertThrows(IllegalStateException.class, p::disqualify);
    }
}
