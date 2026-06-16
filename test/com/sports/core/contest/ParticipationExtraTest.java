package com.sports.core.contest;

import com.sports.core.entity.Participant;
import com.sports.modules.football.EventDerived;
import com.sports.modules.football.TimeLimit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipationExtraTest {

    @Test
    void invalidParticipationTransitionThrows() {
        Participant p = Participant.individual("Alice");
        Participation participation = new Participation(p, null);

        assertThrows(
                IllegalStateException.class,
                () -> participation.complete("1")
        );
    }

    @Test
    void participationKnowsItsContest() {
        Participant a = Participant.individual("A");
        Participant b = Participant.individual("B");

        Participation pa = new Participation(a, "HOME");
        Participation pb = new Participation(b, "AWAY");

        Contest contest = new Contest(List.of(pa, pb), new TimeLimit(90), new EventDerived());

        assertSame(contest, pa.getContest());
        assertSame(contest, pb.getContest());
    }

    @Test
    void participationCannotBelongToTwoContests() {
        Participant a = Participant.individual("A");
        Participant b = Participant.individual("B");

        Participation pa = new Participation(a, "HOME");
        Participation pb = new Participation(b, "AWAY");

        new Contest(List.of(pa, pb), new TimeLimit(90), new EventDerived());

        assertThrows(
                IllegalStateException.class,
                () -> new Contest(List.of(pa, pb), new TimeLimit(90), new EventDerived())
        );
    }
}
