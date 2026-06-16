package com.sports.modules;

import com.sports.core.competition.Competition;
import com.sports.core.competition.Leaderboard;
import com.sports.core.contest.*;
import com.sports.core.entity.Participant;
import com.sports.modules.chess.*;
import com.sports.modules.football.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FootballChessTest {

    // ── Football ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Full football league: winner has most points")
    void footballLeagueStandings() {
        List<Participant> teams = List.of(
            Participant.team("Brazil"), Participant.team("Germany"), Participant.team("France")
        );
        Competition comp = FootballModule.league("Mini League", teams);

        // Brazil beats Germany 2-0
        Contest bg = comp.getContests().get(0);
        bg.start();
        bg.recordEvent(new GoalEvent(bg.getParticipations().get(0), 20));
        bg.recordEvent(new GoalEvent(bg.getParticipations().get(0), 70));
        bg.setElapsedMinutes(90);

        // France beats Brazil 1-0
        Contest fb = comp.getContests().get(1);
        fb.start();
        fb.recordEvent(new GoalEvent(fb.getParticipations().get(1), 55));
        fb.setElapsedMinutes(90);

        // Germany draws France 0-0
        Contest gf = comp.getContests().get(2);
        gf.start();
        gf.setElapsedMinutes(90);

        Leaderboard lb = comp.computeStandings();
        assertEquals(3, lb.getEntries().size());

        double brazilPts  = pts(lb, "Brazil");
        double germanyPts = pts(lb, "Germany");
        double francePts  = pts(lb, "France");
        assertEquals(3.0, brazilPts);   // 1 win
        assertEquals(1.0, germanyPts);  // 1 draw
        assertEquals(4.0, francePts);   // 1 win + 1 draw
    }

    // ── Chess ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Chess Swiss: checkmate gives 1 pt, resignation gives 1 pt")
    void chessSwissStandings() {
        Participant magnus  = Participant.individual("Magnus");
        Participant fabiano = Participant.individual("Fabiano");
        Competition comp = ChessModule.swiss("Mini Swiss", List.of(magnus, fabiano));

        Contest game = comp.getContests().get(0);
        game.start();
        Participation white = game.getParticipations().get(0); // Magnus
        game.recordEvent(new MoveEvent(white, "e4"));
        game.recordEvent(new MoveEvent(white, "Qh5#", TerminationKind.CHECKMATE));

        assertTrue(game.isDecided());
        Leaderboard lb = comp.computeStandings();
        double winnerPts = lb.getEntries().get(0).getPoints();
        assertEquals(1.0, winnerPts);
    }

    @Test
    @DisplayName("Chess draw: both players get 0.5 pts")
    void chessDrawPoints() {
        Participant a = Participant.individual("Alice");
        Participant b = Participant.individual("Bob");
        Competition comp = ChessModule.swiss("Draw Test", List.of(a, b));

        Contest game = comp.getContests().get(0);
        game.start();
        Participation white = game.getParticipations().get(0);
        game.recordEvent(new MoveEvent(white, "draw", TerminationKind.DRAW_AGREED));

        Leaderboard lb = comp.computeStandings();
        lb.getEntries().forEach(e -> assertEquals(0.5, e.getPoints()));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private double pts(Leaderboard lb, String name) {
        return lb.getEntries().stream()
            .filter(e -> e.getParticipant().getName().equals(name))
            .mapToDouble(Leaderboard.Entry::getPoints)
            .findFirst().orElseThrow();
    }
}
