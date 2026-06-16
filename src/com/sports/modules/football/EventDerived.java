package com.sports.modules.football;

import com.sports.core.contest.*;
import com.sports.core.strategy.OutcomeRule;
import java.util.*;

public class EventDerived implements OutcomeRule {

    @Override
    public Result buildResult(Contest contest) {
        Participation dnf = null;
        for (Participation participation : contest.getParticipations()) {
            if (participation.getStatus() == ParticipationStatus.DID_NOT_FINISH
                || participation.getStatus() == ParticipationStatus.DID_NOT_START) {
                dnf = participation;
            }
        }
        if (dnf != null) {
            Participation other = null;
            for (Participation participation : contest.getParticipations()) {
                if (participation != dnf) other = participation;
            }
            Map<Participation, StandingContribution> contributions = new LinkedHashMap<>();
            contributions.put(other, StandingContribution.won());
            contributions.put(dnf, StandingContribution.lost());
            if (other.getStatus() == ParticipationStatus.ACTIVE) {
                other.complete("won");
            }
            return new SimpleResult(List.of(other, dnf), contributions);
        }

        Map<Participation, Integer> goals = new LinkedHashMap<>();

        for (Participation participation : contest.getParticipations()) {
            goals.put(participation, 0);
        }

        for (Object event : contest.getEventLog()) {
            if (event instanceof GoalEvent) {
                GoalEvent goal = (GoalEvent) event;
                Participation scorer = goal.getScorer();

                if (goals.containsKey(scorer)) {
                    goals.put(scorer, goals.get(scorer) + 1);
                }
            }
        }

        for (Participation participation : contest.getParticipations()) {
            if (participation.getStatus() == ParticipationStatus.ACTIVE) {
                participation.complete(String.valueOf(goals.get(participation)));
            }
        }

        List<Participation> ranked = new ArrayList<>(contest.getParticipations());
        ranked.sort((a, b) -> Integer.compare(goals.get(b), goals.get(a)));

        Map<Participation, StandingContribution> contributions = new LinkedHashMap<>();

        if (ranked.size() == 2 && Objects.equals(goals.get(ranked.get(0)), goals.get(ranked.get(1)))) {
            contributions.put(ranked.get(0), StandingContribution.drew());
            contributions.put(ranked.get(1), StandingContribution.drew());
        } else {
            for (int i = 0; i < ranked.size(); i++) {
                Participation participation = ranked.get(i);

                if (i == 0) {
                    contributions.put(participation, StandingContribution.won());
                } else {
                    contributions.put(participation, StandingContribution.lost());
                }
            }
        }

        return new SimpleResult(ranked, contributions);
    }
}