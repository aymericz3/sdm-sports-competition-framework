package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

public class Buchholz implements Tiebreak {
    private final ScoringRule scoringRule;
    private final Tiebreak next;

    public Buchholz(ScoringRule scoringRule, Tiebreak next) {
        this.scoringRule = scoringRule;
        this.next = next;
    }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {

        // Total score of EVERY participant (not just the tied ones): a tied
        // player's Buchholz is the sum of the totals of the opponents it faced,
        // and those opponents are normally outside the tied group.
        Map<Participant, Double> totalScores = new LinkedHashMap<>();
        Map<Participant, Double> buchholz = new LinkedHashMap<>();

        for (Participant participant : tied) {
            buchholz.put(participant, 0.0);
        }

        for (Contest contest : decidedContests) {
            if (contest.getResult() == null) {
                continue;
            }

            for (Participation participation : contest.getParticipations()) {
                Participant participant = participation.getParticipant();
                totalScores.merge(
                    participant,
                    scoringRule.points(contest.getResult().getContribution(participation)),
                    Double::sum
                );
            }
        }

        for (Contest contest : decidedContests) {

            List<Participation> parts = contest.getParticipations();

            if (parts.size() != 2) {
                continue;
            }

            Participant p1 = parts.get(0).getParticipant();
            Participant p2 = parts.get(1).getParticipant();

            if (buchholz.containsKey(p1)) {
                buchholz.put(
                    p1,
                    buchholz.get(p1) + totalScores.getOrDefault(p2, 0.0)
                );
            }

            if (buchholz.containsKey(p2)) {
                buchholz.put(
                    p2,
                    buchholz.get(p2) + totalScores.getOrDefault(p1, 0.0)
                );
            }
        }

        List<Participant> ordered = new ArrayList<>(tied);

        ordered.sort(
            (a, b) -> Double.compare(
                buchholz.get(b),
                buchholz.get(a)
            )
        );

        if (next != null && hasTie(ordered, buchholz)) {
            return next.resolve(ordered, decidedContests);
        }

        return ordered;
    }

    private boolean hasTie(List<Participant> ordered,
                           Map<Participant, Double> values) {

        for (int i = 1; i < ordered.size(); i++) {
            if (Double.compare(
                    values.get(ordered.get(i - 1)),
                    values.get(ordered.get(i))) == 0) {
                return true;
            }
        }

        return false;
    }
}