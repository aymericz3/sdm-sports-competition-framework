package com.sports.modules.common;

import com.sports.core.competition.Leaderboard;
import com.sports.core.contest.Contest;
import com.sports.core.contest.Result;
import com.sports.core.contest.StandingContribution;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.StandingsAggregator;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

public class PointsTable implements StandingsAggregator {

    @Override
    public Leaderboard compute(List<Contest> decidedContests,
                               ScoringRule scoringRule,
                               List<Tiebreak> tiebreaks) {
        Map<Participant, Double> points = new LinkedHashMap<>();

        for (Contest contest : decidedContests) {
            Result result = contest.getResult();
            if (result == null) {
                continue;
            }

            for (var participation : contest.getParticipations()) {
                Participant participant = participation.getParticipant();
                StandingContribution contribution = result.getContribution(participation);

                points.putIfAbsent(participant, 0.0);
                points.put(participant, points.get(participant) + scoringRule.points(contribution));
            }
        }

        List<Participant> ordered = new ArrayList<>(points.keySet());
        ordered.sort((a, b) -> Double.compare(points.get(b), points.get(a)));

        List<Leaderboard.Entry> entries = new ArrayList<>();
        int index = 0;

        while (index < ordered.size()) {
            double groupPoints = points.get(ordered.get(index));
            List<Participant> group = new ArrayList<>();

            while (index < ordered.size()
                    && Double.compare(points.get(ordered.get(index)), groupPoints) == 0) {
                group.add(ordered.get(index));
                index++;
            }

            if (group.size() > 1 && tiebreaks != null && !tiebreaks.isEmpty()) {
                group = tiebreaks.get(0).order(group, decidedContests, scoringRule);
            }

            for (Participant participant : group) {
                entries.add(new Leaderboard.Entry(entries.size() + 1, participant, points.get(participant)));
            }
        }

        return new Leaderboard(entries);
    }
}