package com.sports.modules.chess;

import com.sports.core.contest.*;
import com.sports.core.strategy.OutcomeRule;
import java.util.*;

public class Declared implements OutcomeRule {

    @Override
    public Result buildResult(Contest contest) {
        List<Participation> participations = contest.getParticipations();
        Map<Participation, StandingContribution> contributions = new LinkedHashMap<>();
        List<Participation> ranked = new ArrayList<>();

        TerminationKind kind = contest.getTerminationKind();

        if (kind.isDrawResult()) {
            ranked.addAll(participations);
            for (Participation participation : participations) {
                contributions.put(participation, StandingContribution.drew());
            }
            return new SimpleResult(ranked, contributions);
        }

        Participation loser = null;
        Participation winner = null;

        if (kind == TerminationKind.FORFEIT) {
            for (Participation participation : participations) {
                if (participation.getStatus() == ParticipationStatus.DID_NOT_FINISH) {
                    loser = participation;
                    break;
                }
            }
        } else if (kind == TerminationKind.WALKOVER) {
            for (Participation participation : participations) {
                if (participation.getStatus() == ParticipationStatus.DID_NOT_START) {
                    loser = participation;
                    break;
                }
            }
        } else if (kind == TerminationKind.CHECKMATE || kind == TerminationKind.RESIGNATION) {
            MoveEvent lastMove = lastMove(contest);

            if (lastMove != null) {
                if (kind == TerminationKind.CHECKMATE) {
                    winner = lastMove.getPlayer();
                } else {
                    loser = lastMove.getPlayer();
                }
            }
        }

        if (winner == null && loser != null) {
            for (Participation participation : participations) {
                if (!participation.equals(loser)) {
                    winner = participation;
                    break;
                }
            }
        }

        if (loser == null && winner != null) {
            for (Participation participation : participations) {
                if (!participation.equals(winner)) {
                    loser = participation;
                    break;
                }
            }
        }

        if (winner != null) {
            ranked.add(winner);
            contributions.put(winner, StandingContribution.won());
        }

        if (loser != null) {
            ranked.add(loser);
            contributions.put(loser, StandingContribution.lost());
        }

        for (Participation participation : participations) {
            if (!ranked.contains(participation)) {
                ranked.add(participation);
                contributions.put(participation, StandingContribution.drew());
            }
        }

        return new SimpleResult(ranked, contributions);
    }

    private MoveEvent lastMove(Contest contest) {
        List<Object> events = contest.getEventLog();

        for (int i = events.size() - 1; i >= 0; i--) {
            Object event = events.get(i);

            if (event instanceof MoveEvent) {
                return (MoveEvent) event;
            }
        }

        return null;
    }
}