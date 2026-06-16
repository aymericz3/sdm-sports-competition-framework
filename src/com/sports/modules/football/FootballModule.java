package com.sports.modules.football;

import com.sports.core.competition.Competition;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.modules.common.PointsTable;
import com.sports.modules.common.WinDrawLoss;
import java.util.Arrays;
import java.util.List;

public class FootballModule {

    public static final Sport SPORT = new Sport(
        "Football",
        Arrays.asList("HOME", "AWAY"),
        true,
        () -> new TimeLimit(90),
        EventDerived::new
    );

    public static Competition league(String name, List<Participant> entrants) {
        return new Competition(
            name,
            SPORT,
            entrants,
            new WinDrawLoss(3, 1, 0),
            new RoundRobin(false),
            new PointsTable(),
            Arrays.asList(
                new GoalDifference(
                    new HeadToHead(null)
                )
            ),
            SPORT.newTermination(),
            SPORT.newOutcome()
        );
    }
}