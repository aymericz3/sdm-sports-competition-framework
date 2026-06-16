package com.sports.modules.football;

import com.sports.core.contest.Contest;
import com.sports.core.contest.Participation;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.Tiebreak;
import java.util.*;

public class GoalDifference implements Tiebreak {
    private final Tiebreak next;

    public GoalDifference(Tiebreak next) {
        this.next = next;
    }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        Map<Participant, Integer> goalDifference = new LinkedHashMap<>();

        for (Participant participant : tied) {
            goalDifference.put(participant, 0);
        }

        for (Contest contest : decidedContests) {
            List<Participation> participations = contest.getParticipations();

            if (participations.size() != 2) {
                continue;
            }

            Participation first = participations.get(0);
            Participation second = participations.get(1);

            if (!goalDifference.containsKey(first.getParticipant()) &&
                !goalDifference.containsKey(second.getParticipant())) {
                continue;
            }

            int firstGoals = parseGoals(first.getOutcomeValue());
            int secondGoals = parseGoals(second.getOutcomeValue());

            if (goalDifference.containsKey(first.getParticipant())) {
                goalDifference.put(
                        first.getParticipant(),
                        goalDifference.get(first.getParticipant()) + firstGoals - secondGoals
                );
            }

            if (goalDifference.containsKey(second.getParticipant())) {
                goalDifference.put(
                        second.getParticipant(),
                        goalDifference.get(second.getParticipant()) + secondGoals - firstGoals
                );
            }
        }

        List<Participant> ordered = new ArrayList<>(tied);
        ordered.sort((a, b) -> Integer.compare(goalDifference.get(b), goalDifference.get(a)));

        if (next != null && hasTie(ordered, goalDifference)) {
            return next.resolve(ordered, decidedContests);
        }

        return ordered;
    }

    private int parseGoals(String value) {
        if (value == null) {
            return 0;
        }

        return Integer.parseInt(value);
    }

    private boolean hasTie(List<Participant> ordered, Map<Participant, Integer> values) {
        for (int i = 1; i < ordered.size(); i++) {
            if (Objects.equals(values.get(ordered.get(i - 1)), values.get(ordered.get(i)))) {
                return true;
            }
        }

        return false;
    }
}