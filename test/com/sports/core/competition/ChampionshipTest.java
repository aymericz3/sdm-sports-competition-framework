package com.sports.core.competition;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.contest.TerminationKind;
import com.sports.core.entity.Participant;
import com.sports.modules.chess.ChessModule;
import com.sports.modules.chess.MoveEvent;
import com.sports.modules.common.MedalTable;
import com.sports.modules.football.FootballModule;
import com.sports.modules.football.GoalEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Multi-level competition (the teacher's Olympics / world-championship case),
 * realised with the Composite pattern: a Championship aggregates heterogeneous
 * events into one medal table, and may itself contain sub-championships.
 */
class ChampionshipTest {

    /** USA wins both a football league and a chess game, over the same nations. */
    private static Competition footballWonByUsa(Participant usa, Participant nor) {
        Competition football = FootballModule.league("Football", List.of(usa, nor));
        Contest fb = football.getContests().get(0);   // USA = HOME
        fb.start();
        fb.recordEvent(new GoalEvent(fb.getParticipations().get(0), 30));
        fb.setElapsedMinutes(90);
        return football;
    }

    private static Competition chessWonByUsa(Participant usa, Participant nor) {
        Competition chess = ChessModule.swiss("Chess", List.of(usa, nor));
        Contest game = chess.getContests().get(0);
        game.start();
        Participation white = game.getParticipations().get(0); // USA = first entrant
        game.recordEvent(new MoveEvent(white, "Qh5#", TerminationKind.CHECKMATE));
        return chess;
    }

    @Test
    @DisplayName("Medal table aggregates wins across heterogeneous events")
    void medalTableAggregatesEvents() {
        Participant usa = Participant.team("USA");
        Participant nor = Participant.team("Norway");

        Championship olympics = new Championship(
                "Mini Olympics",
                List.of(footballWonByUsa(usa, nor), chessWonByUsa(usa, nor)),
                new MedalTable());

        List<Leaderboard.Entry> medals = olympics.computeStandings().getEntries();
        assertEquals("USA", medals.get(0).getParticipant().getName());
        assertEquals(2.0, medals.get(0).getPoints());   // gold in both events
        assertEquals("Norway", medals.get(1).getParticipant().getName());
        assertEquals(0.0, medals.get(1).getPoints());   // no gold
    }

    @Test
    @DisplayName("A championship can contain another championship (Composite nesting)")
    void championshipCanNest() {
        Participant usa = Participant.team("USA");
        Participant nor = Participant.team("Norway");

        // A sub-championship is itself a Standings, so it nests inside the games uniformly.
        Standings ballSports = new Championship(
                "Ball Sports", List.of(footballWonByUsa(usa, nor)), new MedalTable());
        Championship games = new Championship(
                "Games", List.of(ballSports, chessWonByUsa(usa, nor)), new MedalTable());

        List<Leaderboard.Entry> medals = games.computeStandings().getEntries();
        assertEquals("USA", medals.get(0).getParticipant().getName());
        assertEquals(2.0, medals.get(0).getPoints());
    }
}
