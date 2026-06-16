package com.sports.modules.chess;

import com.sports.core.competition.Competition;
import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.contest.TerminationKind;
import com.sports.core.entity.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the adaptive Swiss format (report §3.3.4 and Decision Flow 3): the next
 * round is generated only from decided standings, cannot be emitted before the
 * current round is complete, and never repeats a pairing.
 */
class SwissPairingTest {

    private static List<Participant> fourPlayers() {
        return List.of(
            Participant.individual("A"), Participant.individual("B"),
            Participant.individual("C"), Participant.individual("D"));
    }

    /** Decides every still-open contest: the white player (index 0) checkmates. */
    private static void decideOpenContests(Competition comp) {
        for (Contest c : comp.getContests()) {
            if (!c.isDecided()) {
                c.start();
                Participation white = c.getParticipations().get(0);
                c.recordEvent(new MoveEvent(white, "mate", TerminationKind.CHECKMATE));
            }
        }
    }

    private static Set<String> pairings(List<Contest> contests) {
        Set<String> pairs = new HashSet<>();
        for (Contest c : contests) {
            List<Participation> ps = c.getParticipations();
            String a = ps.get(0).getParticipant().getId();
            String b = ps.get(1).getParticipant().getId();
            pairs.add(a.compareTo(b) < 0 ? a + "|" + b : b + "|" + a);
        }
        return pairs;
    }

    @Test
    @DisplayName("Next round is generated once the current round is decided")
    void generateNextRoundProducesNewContests() {
        Competition swiss = ChessModule.swiss("Swiss", fourPlayers());
        assertEquals(2, swiss.getContests().size(), "round 1 pairs 4 players into 2 games");

        decideOpenContests(swiss);
        swiss.generateNextRound();

        assertEquals(4, swiss.getContests().size(), "round 2 adds two more games");
    }

    @Test
    @DisplayName("Next round cannot be emitted while the current round is incomplete")
    void generateNextRoundThrowsIfRoundIncomplete() {
        Competition swiss = ChessModule.swiss("Swiss", fourPlayers());
        // Round 1 left undecided on purpose.
        assertThrows(IllegalStateException.class, swiss::generateNextRound);
    }

    @Test
    @DisplayName("Swiss pairing avoids rematches across rounds")
    void swissAvoidsRematches() {
        Competition swiss = ChessModule.swiss("Swiss", fourPlayers());

        Set<String> round1 = pairings(swiss.getContests());
        decideOpenContests(swiss);
        swiss.generateNextRound();

        List<Contest> all = swiss.getContests();
        Set<String> round2 = pairings(all.subList(2, all.size()));

        for (String pair : round2) {
            assertFalse(round1.contains(pair), "round 2 must not repeat a round 1 pairing");
        }
    }
}
