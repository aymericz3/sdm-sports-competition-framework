# Composite

## Intent
Compose objects into tree structures and let clients treat individual objects
(leaves) and compositions of objects (containers) uniformly through one type.

## Where it lives
The competition tier. A multi-level competition — the Olympics, a world
championship — is a tree: **Games → sports → events**, where each leaf event
produces its own standings and each parent aggregates its children into one
overall table. The pattern lets a single competition and a whole championship be
built and queried the same way.

- Component: [Standings.java](../../src/com/sports/core/competition/Standings.java)
  — `getName()` + `Leaderboard computeStandings()`.
- Leaf: [Competition.java](../../src/com/sports/core/competition/Competition.java)
  `implements Standings` — one format over one entrant set.
- Composite: [Championship.java](../../src/com/sports/core/competition/Championship.java)
  — holds child `Standings` and aggregates them.

## Participants
- **Component:** `Standings` (the common type).
- **Leaf:** `Competition` — a league, a Swiss event.
- **Composite:** `Championship` — holds `List<Standings>` (competitions *or*
  nested championships) and an `OverallAggregator` strategy.

## How the composite produces a result
A championship delegates to its aggregator, which reads each child's leaderboard
— without caring whether that child is a leaf or another composite:

```java
// Championship.computeStandings()
return aggregator.aggregate(events);   // events : List<Standings>
```

```java
// MedalTable.aggregate(...) — gold/silver/bronze per child, ranked by golds
for (Standings event : children) {
    for (Leaderboard.Entry e : event.computeStandings().getEntries()) {
        if (e.getRank() == 1) medals.gold++;
        ...
    }
}
```

Because `Competition` and `Championship` both implement `Standings`, a
`Championship` can contain another `Championship` (Games → "Ball sports" →
football), and the aggregator code never changes. Both cases are covered by
`ChampionshipTest`.

## Why this is the right tier for Composite
The report applied Composite to `Contest` for *tennis* (match → set → game), a
stress-test probe that is **not** one of the two delivered disciplines, so that
nesting would have been dead code. Composite is instead realised where it does
real work and answers the brief's explicit requirement: the multi-level
competition. Football and chess remain atomic single contests; the medal table
is what binds several of them into a Games.

## How to extend
- A different overall ranking (total points, weighted score) is a new
  [`OverallAggregator`](../../src/com/sports/core/strategy/OverallAggregator.java),
  not a change to `Championship`.
- Arbitrary depth comes for free: nest `Championship`s inside `Championship`s.
