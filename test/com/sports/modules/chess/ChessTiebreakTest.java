package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.contest.TerminationKind;
import com.sports.core.entity.Participant;
import com.sports.modules.common.WinDrawLoss;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the chess Buchholz tiebreak (report §3.3.6: "sum of opponents' scores").
 * Two players level on points are separated by the strength of the opponents
 * they faced — opponents who are themselves outside the tied group.
 */
class ChessTiebreakTest {

    private final WinDrawLoss scoring = new WinDrawLoss(1, 0.5, 0);

    /** Plays a chess game won by {@code winner} (checkmate) and returns the decided contest. */
    private static Contest win(Participant winner, Participant loser) {
        Participation w = new Participation(winner, "WHITE");
        Participation b = new Participation(loser, "BLACK");
        Contest c = new Contest(List.of(w, b), new TerminalCondition(), new Declared());
        c.start();
        c.recordEvent(new MoveEvent(w, "mate", TerminationKind.CHECKMATE));
        return c;
    }

    @Test
    @DisplayName("Buchholz breaks a points tie by opponents' strength")
    void buchholzBreaksPointsTie() {
        Participant a = Participant.individual("A");
        Participant b = Participant.individual("B");
        Participant c = Participant.individual("C");
        Participant d = Participant.individual("D");
        Participant e = Participant.individual("E");
        Participant f = Participant.individual("F");

        // A and B each win both their games (2 pts -> tied for first).
        // A's opponents (C, D) each also win a game (1 pt each)  -> Buchholz 2.
        // B's opponents (E, F) lose all their games (0 pts each) -> Buchholz 0.
        List<Contest> decided = List.of(
            win(a, c), win(a, d),   // A beats C and D
            win(b, e), win(b, f),   // B beats E and F
            win(c, e), win(d, f)    // C and D each pick up a win; E and F stay on 0
        );

        List<Participant> ordered =
            new Buchholz(scoring, null).resolve(List.of(b, a), decided);

        assertEquals(a, ordered.get(0), "A faced stronger opponents, so wins the Buchholz tiebreak");
        assertEquals(b, ordered.get(1));
    }

    @Test
    @DisplayName("Sonneborn-Berger breaks a points tie by beaten-opponents' strength")
    void sonnebornBergerBreaksPointsTie() {
        Participant a = Participant.individual("A");
        Participant b = Participant.individual("B");
        Participant c = Participant.individual("C");
        Participant d = Participant.individual("D");
        Participant e = Participant.individual("E");
        Participant f = Participant.individual("F");

        // Same fixture as above: A and B both on 2 pts, but A beat opponents who
        // went on to score (C, D = 1 pt each) while B beat winless ones (E, F = 0).
        List<Contest> decided = List.of(
            win(a, c), win(a, d),
            win(b, e), win(b, f),
            win(c, e), win(d, f)
        );

        List<Participant> ordered =
            new SonnebornBerger(scoring).resolve(List.of(b, a), decided);

        assertEquals(a, ordered.get(0), "A beat stronger opponents, so wins the Sonneborn-Berger tiebreak");
        assertEquals(b, ordered.get(1));
    }
}
