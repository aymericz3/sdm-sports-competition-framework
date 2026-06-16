package com.sports.core.contest;

import com.sports.core.entity.Participant;
import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.TerminationRule;
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
    void addChildLinksNestedContest() {

        Participant p1 = Participant.individual("A");
        Participant p2 = Participant.individual("B");

        Participation pa = new Participation(p1, null);
        Participation pb = new Participation(p2, null);

        TerminationRule tRule = contest -> TerminationKind.NOT_OVER;
        OutcomeRule oRule = contest -> null;

        Contest parent = new Contest(
                List.of(pa, pb),
                tRule,
                oRule
        );

        Contest child = new Contest(
                List.of(
                        new Participation(p1, null),
                        new Participation(p2, null)
                ),
                tRule,
                oRule
        );

        parent.addChild(child);

        assertEquals(1, parent.getChildren().size());
        assertSame(child, parent.getChildren().get(0));
    }
}