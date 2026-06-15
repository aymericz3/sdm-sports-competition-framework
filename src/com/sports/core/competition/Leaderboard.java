package com.sports.core.competition;

import com.sports.core.entity.Participant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard {

    public static class Entry {
        private final int rank;
        private final Participant participant;
        private final double points;

        public Entry(int rank, Participant participant, double points) {
            this.rank = rank;
            this.participant = participant;
            this.points = points;
        }

        public int getRank()              { return rank; }
        public Participant getParticipant() { return participant; }
        public double getPoints()         { return points; }

        @Override
        public String toString() {
            return rank + ". " + participant.getName() + " — " + points + " pts";
        }
    }

    private final List<Entry> entries;

    public Leaderboard(List<Entry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public List<Entry> getEntries() { return entries; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry e : entries) sb.append(e).append('\n');
        return sb.toString().trim();
    }
}
