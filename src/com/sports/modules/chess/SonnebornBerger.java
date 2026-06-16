package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.ScoringRule;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

public class SonnebornBerger implements Tiebreak {
    private final ScoringRule scoringRule;

    public SonnebornBerger(ScoringRule scoringRule) {
        this.scoringRule = scoringRule;
    }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        Map<Participant, Double> totalScores = new LinkedHashMap<>();
        Map<Participant, Double> sbScores = new LinkedHashMap<>();

        for (Participant participant : tied) {
            totalScores.put(participant, 0.0);
            sbScores.put(participant, 0.0);
        }

        for (Contest contest : decidedContests) {
            if (contest.getResult() == null) {
                continue;
            }

            for (Participation participation : contest.getParticipations()) {
                Participant participant = participation.getParticipant();

                if (totalScores.containsKey(participant)) {
                    totalScores.put(
                        participant,
                        totalScores.get(participant)
                            + scoringRule.points(contest.getResult().getContribution(participation))
                    );
                }
            }
        }

        for (Contest contest : decidedContests) {
            if (contest.getResult() == null || contest.getParticipations().size() != 2) {
                continue;
            }

            Participation first = contest.getParticipations().get(0);
            Participation second = contest.getParticipations().get(1);

            addScore(first, second, contest, totalScores, sbScores);
            addScore(second, first, contest, totalScores, sbScores);
        }

        List<Participant> ordered = new ArrayList<>(tied);
        ordered.sort((a, b) -> Double.compare(sbScores.get(b), sbScores.get(a)));

        return ordered;
    }

    private void addScore(Participation player,
                          Participation opponent,
                          Contest contest,
                          Map<Participant, Double> totalScores,
                          Map<Participant, Double> sbScores) {
        Participant participant = player.getParticipant();
        Participant opponentParticipant = opponent.getParticipant();

        if (!sbScores.containsKey(participant)) {
            return;
        }

        double scoreAgainstOpponent =
            scoringRule.points(contest.getResult().getContribution(player));

        double opponentTotalScore =
            totalScores.getOrDefault(opponentParticipant, 0.0);

        sbScores.put(
            participant,
            sbScores.get(participant) + scoreAgainstOpponent * opponentTotalScore
        );
    }
}