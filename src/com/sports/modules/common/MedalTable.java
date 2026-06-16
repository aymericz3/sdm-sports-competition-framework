package com.sports.modules.common;

import com.sports.core.competition.Leaderboard;
import com.sports.core.competition.Standings;
import com.sports.core.entity.Participant;
import com.sports.core.strategy.OverallAggregator;
import java.util.*;

/**
 * Olympic-style overall aggregator: the winner of each child event earns a gold,
 * the runner-up a silver, third place a bronze. Participants are then ranked by
 * golds, then silvers, then bronzes — the standard medal-table order. The
 * leaderboard's points column carries the gold count.
 *
 * <p>Like every aggregator it is a pure function of its children's decided
 * results, so the medal table is always recomputable and never cached.
 */
public class MedalTable implements OverallAggregator {

    private static final class Medals {
        int gold, silver, bronze;
    }

    @Override
    public Leaderboard aggregate(List<Standings> children) {
        Map<Participant, Medals> tally = new LinkedHashMap<>();

        for (Standings event : children) {
            List<Leaderboard.Entry> entries = event.computeStandings().getEntries();

            for (Leaderboard.Entry entry : entries) {
                Medals medals = tally.computeIfAbsent(entry.getParticipant(), p -> new Medals());

                int rank = entry.getRank();
                if (rank == 1)      medals.gold++;
                else if (rank == 2) medals.silver++;
                else if (rank == 3) medals.bronze++;
            }
        }

        List<Participant> ordered = new ArrayList<>(tally.keySet());
        ordered.sort((a, b) -> {
            Medals ma = tally.get(a);
            Medals mb = tally.get(b);
            if (mb.gold != ma.gold)     return Integer.compare(mb.gold, ma.gold);
            if (mb.silver != ma.silver) return Integer.compare(mb.silver, ma.silver);
            return Integer.compare(mb.bronze, ma.bronze);
        });

        List<Leaderboard.Entry> table = new ArrayList<>();
        for (int i = 0; i < ordered.size(); i++) {
            table.add(new Leaderboard.Entry(i + 1, ordered.get(i), tally.get(ordered.get(i)).gold));
        }

        return new Leaderboard(table);
    }
}
