# Strategy

## Intent
Define a family of interchangeable algorithms, encapsulate each one behind a
common interface, and make them selectable per discipline or per competition
without changing the code that uses them.

## Why it is the backbone of this framework
The whole redesign rests on one claim: *adding a discipline means writing
strategies, not editing the core.* Every way in which football and chess differ
is expressed as a different object plugged into the **same** `Contest` and
`Competition` classes — never as a subclass. Strategy is the mechanism that makes
that possible.

Two strategies configure a single contest, four configure a whole competition:

| Strategy interface | Question it answers | File |
| --- | --- | --- |
| `TerminationRule` | When is a contest over? | [TerminationRule.java](../../src/com/sports/core/strategy/TerminationRule.java) |
| `OutcomeRule` | What is the result of a finished contest? | [OutcomeRule.java](../../src/com/sports/core/strategy/OutcomeRule.java) |
| `ScoringRule` | How many points does a standing-contribution earn? | [ScoringRule.java](../../src/com/sports/core/strategy/ScoringRule.java) |
| `FixtureGenerator` | Which contests exist, and when? | [FixtureGenerator.java](../../src/com/sports/core/strategy/FixtureGenerator.java) |
| `StandingsAggregator` | How is the leaderboard built? | [StandingsAggregator.java](../../src/com/sports/core/strategy/StandingsAggregator.java) |
| `Tiebreak` | How are level participants ordered? | [Tiebreak.java](../../src/com/sports/core/strategy/Tiebreak.java) |

A seventh strategy, `OverallAggregator`
([OverallAggregator.java](../../src/com/sports/core/strategy/OverallAggregator.java)),
serves the multi-level tier: it combines several events' leaderboards into one
overall table (e.g. `MedalTable`). See [Composite](composite.md).

The discipline's **default** `TerminationRule` and `OutcomeRule` are supplied by
the [`Sport`](../../src/com/sports/core/entity/Sport.java) profile
(`sport.newTermination()`, `sport.newOutcome()`), injected by the module — so the
"which rules a contest uses" decision lives on the sport, while scoring, fixtures
and tiebreak remain owned by the competition.

## Participants
- **Strategy (interface):** the six interfaces above.
- **Concrete strategies:** the module classes that implement them —
  `TimeLimit`, `EventDerived` (football); `TerminalCondition`, `Declared`
  (chess); `WinDrawLoss`, `PointsTable` (shared); `RoundRobin`, `SwissPairing`,
  and the tiebreaks.
- **Context:** [`Contest`](../../src/com/sports/core/contest/Contest.java) holds a
  `TerminationRule` and an `OutcomeRule`;
  [`Competition`](../../src/com/sports/core/competition/Competition.java) holds the
  other four. Both call their strategies and never branch on the sport.

## How the context delegates
`Contest` never decides anything itself — it asks:

```java
// Contest.checkTermination()
TerminationKind kind = terminationRule.isOver(this);
...
// Contest.finalise()
result = outcomeRule.buildResult(this);
```

Termination and outcome stay two separate strategies even when one real-world
event answers both (a chess checkmate ends the game *and* names the winner). That
separation is what lets a football match (whistle ends it, goal tally decides it)
and a marathon (cutoff ends it, measured times decide it) reuse the identical
`Contest` object with different rules plugged in.

## Mapping a discipline = choosing a bundle
- **Football** = `TimeLimit` + `EventDerived` + `WinDrawLoss(3,1,0)` +
  `RoundRobin` + `PointsTable` + `GoalDifference`.
- **Chess** = `TerminalCondition` + `Declared` + `WinDrawLoss(1,0.5,0)` +
  `SwissPairing` + `PointsTable` + `Buchholz`.

The framework code is identical across both; only the injected objects change.

## How to extend
Add a discipline by writing new implementations of these interfaces and bundling
them in a module (see [Facade](facade.md)). No core class is touched.
