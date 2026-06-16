package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.FixtureGenerator;
import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.TerminationRule;
import java.util.ArrayList;
import java.util.List;

public class RoundRobin implements FixtureGenerator {
    private final boolean homeAndAway;

    public RoundRobin(boolean homeAndAway) {
        this.homeAndAway = homeAndAway;
    }

    @Override
    public boolean isAdaptive() {
        return false;
    }

    @Override
    public List<Contest> generateAll(List<Participant> entrants,
                                     TerminationRule tRule,
                                     OutcomeRule oRule) {

        List<Contest> fixtures = new ArrayList<>();

        for (int i = 0; i < entrants.size(); i++) {
            for (int j = i + 1; j < entrants.size(); j++) {

                Participant home = entrants.get(i);
                Participant away = entrants.get(j);

                List<Participation> match = new ArrayList<>();
                match.add(new Participation(home, "HOME"));
                match.add(new Participation(away, "AWAY"));

                fixtures.add(new Contest(match, tRule, oRule));

                if (homeAndAway) {
                    List<Participation> reverse = new ArrayList<>();
                    reverse.add(new Participation(away, "HOME"));
                    reverse.add(new Participation(home, "AWAY"));

                    fixtures.add(new Contest(reverse, tRule, oRule));
                }
            }
        }

        return fixtures;
    }

    @Override
    public List<Contest> nextRound(List<Participant> entrants,
                                   List<Contest> decided,
                                   ScoringRule sr,
                                   TerminationRule tRule,
                                   OutcomeRule oRule) {

        return new ArrayList<>();
    }
}