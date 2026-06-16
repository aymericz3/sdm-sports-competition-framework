package com.sports.modules.chess;

import com.sports.core.competition.Competition;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.modules.common.PointsTable;
import com.sports.modules.common.WinDrawLoss;
import java.util.Arrays;
import java.util.List;

public class ChessModule {

    public static final Sport SPORT = new Sport(
        "Chess",
        Arrays.asList("WHITE", "BLACK"),
        true,
        TerminalCondition::new,
        Declared::new
    );

    public static Competition swiss(String name, List<Participant> entrants) {
        WinDrawLoss scoring = new WinDrawLoss(1, 0.5, 0);

        return new Competition(
            name,
            SPORT,
            entrants,
            scoring,
            new SwissPairing(),
            new PointsTable(),
            Arrays.asList(
                new Buchholz(
                    scoring,
                    new SonnebornBerger(scoring)
                )
            ),
            SPORT.newTermination(),
            SPORT.newOutcome()
        );
    }
}