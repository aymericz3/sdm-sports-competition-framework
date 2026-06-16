package com.sports.core.competition;

import com.sports.core.contest.Contest;
import com.sports.core.entity.Participant;
import com.sports.core.entity.Sport;
import com.sports.core.strategy.*;
import java.util.*;

public class Competition {
    private final String id;
    private final String name;
    private final Sport sport;
    private final List<Participant> entrants;
    private final List<Contest> contests;
    private final ScoringRule scoringRule;
    private final FixtureGenerator fixtureGenerator;
    private final StandingsAggregator standingsAggregator;
    private final List<Tiebreak> tiebreaks;
    private final TerminationRule cachedTRule;
    private final OutcomeRule cachedORule;

    public Competition(String name,
                       Sport sport,
                       List<Participant> entrants,
                       ScoringRule scoringRule,
                       FixtureGenerator fixtureGenerator,
                       StandingsAggregator standingsAggregator,
                       List<Tiebreak> tiebreaks,
                       TerminationRule tRule,
                       OutcomeRule oRule) {
        if (entrants == null || entrants.size() < 2) {
            throw new IllegalArgumentException("A competition needs at least two entrants");
        }

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.sport = sport;
        this.entrants = new ArrayList<>(entrants);
        this.contests = new ArrayList<>();
        this.scoringRule = scoringRule;
        this.fixtureGenerator = fixtureGenerator;
        this.standingsAggregator = standingsAggregator;
        this.tiebreaks = tiebreaks == null ? new ArrayList<>() : new ArrayList<>(tiebreaks);
        this.cachedTRule = tRule;
        this.cachedORule = oRule;

        if (fixtureGenerator.isAdaptive()) {
            contests.addAll(fixtureGenerator.nextRound(
                    this.entrants,
                    getDecidedContests(),
                    scoringRule,
                    cachedTRule,
                    cachedORule
            ));
        } else {
            contests.addAll(fixtureGenerator.generateAll(
                    this.entrants,
                    cachedTRule,
                    cachedORule
            ));
        }
    }

    public String getId()                       { return id; }
    public String getName()                     { return name; }
    public Sport getSport()                     { return sport; }
    public List<Participant> getEntrants()      { return Collections.unmodifiableList(entrants); }
    public List<Contest> getContests()          { return Collections.unmodifiableList(contests); }
    public ScoringRule getScoringRule()         { return scoringRule; }
    public FixtureGenerator getFixtureGenerator() { return fixtureGenerator; }
    public List<Tiebreak> getTiebreaks()        { return Collections.unmodifiableList(tiebreaks); }

    public List<Contest> getDecidedContests() {
        List<Contest> decided = new ArrayList<>();
        for (Contest c : contests) {
            if (c.isDecided()) {
                decided.add(c);
            }
        }
        return decided;
    }

    public Leaderboard computeStandings() {
        return standingsAggregator.compute(
                getDecidedContests(),
                scoringRule,
                Collections.unmodifiableList(tiebreaks)
        );
    }

    public void generateNextRound() {
        if (!fixtureGenerator.isAdaptive()) {
            throw new IllegalStateException("Only adaptive competitions can generate next rounds");
        }

        for (Contest contest : contests) {
            if (!contest.isDecided()) {
                throw new IllegalStateException("Current round must be complete before generating the next round");
            }
        }

        contests.addAll(fixtureGenerator.nextRound(
                entrants,
                getDecidedContests(),
                scoringRule,
                cachedTRule,
                cachedORule
        ));
    }

    public void addContest(Contest contest) {
        contests.add(contest);
    }

    @Override
    public String toString() {
        return name;
    }
}