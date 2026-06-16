package com.sports.modules.chess;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.contest.Result;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.*;
import java.util.*;

public class SwissPairing implements FixtureGenerator {

    @Override
    public boolean isAdaptive() {
        return true;
    }

    @Override
    public List<Contest> generateAll(List<Participant> entrants,
                                     TerminationRule tRule,
                                     OutcomeRule oRule) {
        return pairInOrder(new ArrayList<>(entrants), tRule, oRule);
    }

    @Override
    public List<Contest> nextRound(List<Participant> entrants,
                                   List<Contest> decidedContests,
                                   ScoringRule scoringRule,
                                   TerminationRule tRule,
                                   OutcomeRule oRule) {

        Map<Participant, Double> scores = scores(entrants, decidedContests, scoringRule);

        List<Participant> ordered = new ArrayList<>(entrants);
        ordered.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));

        return pairAvoidingRematches(ordered, decidedContests, tRule, oRule);
    }

    private List<Contest> pairInOrder(List<Participant> ordered,
                                      TerminationRule tRule,
                                      OutcomeRule oRule) {
        List<Contest> contests = new ArrayList<>();

        for (int i = 0; i + 1 < ordered.size(); i += 2) {
            contests.add(makeContest(ordered.get(i), ordered.get(i + 1), tRule, oRule));
        }

        return contests;
    }

    private List<Contest> pairAvoidingRematches(List<Participant> ordered,
                                                List<Contest> decidedContests,
                                                TerminationRule tRule,
                                                OutcomeRule oRule) {
        List<Contest> contests = new ArrayList<>();
        List<Participant> remaining = new ArrayList<>(ordered);

        while (remaining.size() >= 2) {
            Participant first = remaining.remove(0);
            int opponentIndex = 0;

            for (int i = 0; i < remaining.size(); i++) {
                if (!alreadyPlayed(first, remaining.get(i), decidedContests)) {
                    opponentIndex = i;
                    break;
                }
            }

            Participant second = remaining.remove(opponentIndex);
            contests.add(makeContest(first, second, tRule, oRule));
        }

        return contests;
    }

    private Contest makeContest(Participant white,
                                Participant black,
                                TerminationRule tRule,
                                OutcomeRule oRule) {
        List<Participation> participations = new ArrayList<>();
        participations.add(new Participation(white, "WHITE"));
        participations.add(new Participation(black, "BLACK"));

        return new Contest(participations, tRule, oRule);
    }

    private boolean alreadyPlayed(Participant a,
                                  Participant b,
                                  List<Contest> decidedContests) {
        for (Contest contest : decidedContests) {
            boolean hasA = false;
            boolean hasB = false;

            for (Participation participation : contest.getParticipations()) {
                if (participation.getParticipant().equals(a)) {
                    hasA = true;
                }

                if (participation.getParticipant().equals(b)) {
                    hasB = true;
                }
            }

            if (hasA && hasB) {
                return true;
            }
        }

        return false;
    }

    private Map<Participant, Double> scores(List<Participant> entrants,
                                            List<Contest> decidedContests,
                                            ScoringRule scoringRule) {
        Map<Participant, Double> scores = new LinkedHashMap<>();

        for (Participant entrant : entrants) {
            scores.put(entrant, 0.0);
        }

        for (Contest contest : decidedContests) {
            Result result = contest.getResult();

            if (result == null) {
                continue;
            }

            for (Participation participation : contest.getParticipations()) {
                Participant participant = participation.getParticipant();

                if (scores.containsKey(participant)) {
                    scores.put(
                        participant,
                        scores.get(participant)
                            + scoringRule.points(result.getContribution(participation))
                    );
                }
            }
        }

        return scores;
    }
}