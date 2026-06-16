package com.sports.core.competition;

import com.sports.core.strategy.OverallAggregator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The Composite node of the competition tier: a multi-level competition such as
 * the Olympics or a world championship. It holds child {@link Standings} — which
 * may themselves be single {@link Competition}s (leaves) or further
 * {@code Championship}s (sub-composites) — and produces one overall leaderboard
 * by delegating to its {@link OverallAggregator}.
 *
 * <p>Because both leaf and composite implement {@link Standings}, a championship
 * is built and queried exactly like a single competition.
 */
public class Championship implements Standings {
    private final String id;
    private final String name;
    private final List<Standings> events;
    private final OverallAggregator aggregator;

    public Championship(String name, List<Standings> events, OverallAggregator aggregator) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("A championship needs at least one event");
        }

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.events = new ArrayList<>(events);
        this.aggregator = aggregator;
    }

    public String getId()                 { return id; }
    @Override public String getName()     { return name; }
    public List<Standings> getEvents()    { return Collections.unmodifiableList(events); }

    public void addEvent(Standings event) {
        events.add(event);
    }

    @Override
    public Leaderboard computeStandings() {
        return aggregator.aggregate(Collections.unmodifiableList(events));
    }

    @Override
    public String toString() {
        return name;
    }
}
