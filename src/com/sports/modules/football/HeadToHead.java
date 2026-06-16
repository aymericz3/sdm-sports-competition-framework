package com.sports.modules.football;

import com.sports.core.contest.*;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.Tiebreak;
import com.sports.modules.common.WinDrawLoss;
import java.util.*;

public class HeadToHead implements Tiebreak {
    private final Tiebreak next;

    public HeadToHead(Tiebreak next) {
        this.next = next;
    }

    @Override
    public List<Participant> resolve(List<Participant> tied, List<Contest> decidedContests) {
        Map<Participant, Double> points = new LinkedHashMap<>();
        WinDrawLoss scoring = new WinDrawLoss(3, 1, 0);

        for (Participant participant : tied) {
            points.put(participant, 0.0);
        }

        for (Contest contest : decidedContests) {
            Result result = contest.getResult();

            if (result == null) {
                continue;
            }

            boolean onlyTiedParticipants = true;

            for (Participation participation : contest.getParticipations()) {
                if (!points.containsKey(participation.getParticipant())) {
                    onlyTiedParticipants = false;
                    break;
                }
            }

            if (!onlyTiedParticipants) {
                continue;
            }

            for (Participation participation : contest.getParticipations()) {
                Participant participant = participation.getParticipant();
                StandingContribution contribution = result.getContribution(participation);

                points.put(participant, points.get(participant) + scoring.points(contribution));
            }
        }

        List<Participant> ordered = new ArrayList<>(tied);
        ordered.sort((a, b) -> Double.compare(points.get(b), points.get(a)));

        if (next != null && hasTie(ordered, points)) {
            return next.resolve(ordered, decidedContests);
        }

        return ordered;
    }

    private boolean hasTie(List<Participant> ordered, Map<Participant, Double> values) {
        for (int i = 1; i < ordered.size(); i++) {
            if (Double.compare(values.get(ordered.get(i - 1)), values.get(ordered.get(i))) == 0) {
                return true;
            }
        }

        return false;
    }
}