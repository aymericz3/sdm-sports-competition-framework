# Chain of Responsibility

## Intent
Chain a set of handlers so that each one either resolves the request or passes
the unresolved remainder to the next handler in the chain — decoupling the
caller from the number and order of handlers.

## Where it lives
Tiebreaking. Two participants level on points must be ordered by a sequence of
criteria, each applied only to whatever the previous one left tied. Each
`Tiebreak` holds a reference to the `next` link and decides whether to forward.

- Interface: [Tiebreak.java](../../src/com/sports/core/strategy/Tiebreak.java)
- Links (football): [GoalDifference.java](../../src/com/sports/modules/football/GoalDifference.java),
  [HeadToHead.java](../../src/com/sports/modules/football/HeadToHead.java)
- Links (chess): [Buchholz.java](../../src/com/sports/modules/chess/Buchholz.java),
  [SonnebornBerger.java](../../src/com/sports/modules/chess/SonnebornBerger.java)

## Participants
- **Handler:** `Tiebreak` — `List<Participant> resolve(tied, decidedContests)`.
- **Concrete handlers:** each criterion above, holding a `Tiebreak next`.
- **Client:** [`PointsTable`](../../src/com/sports/modules/common/PointsTable.java)
  invokes the head of the chain on each group of level participants.

## How a link forwards
A link orders the tied group by its own criterion; if a tie remains *and* it has
a successor, it delegates the still-tied ordering onward:

```java
// GoalDifference.resolve(...)
List<Participant> ordered = ... // sorted by goal difference
if (next != null && hasTie(ordered, goalDifference)) {
    return next.resolve(ordered, decidedContests);
}
return ordered;
```

## How the chain is assembled
The chain is built once, in the module, as nested constructor arguments:

```java
// FootballModule.league(...)
new GoalDifference(new HeadToHead(null))   // goal difference, then head-to-head

// ChessModule.swiss(...)
new Buchholz(scoring, new SonnebornBerger(scoring))   // Buchholz, then Sonneborn-Berger
```

A terminal `null` link means "leave a genuine tie." A tiebreak never changes
points and reads only decided results, so the standings stay recomputable.

## How to extend
Write a new `Tiebreak`, give it a `next`, and splice it into the chain a module
builds. The aggregator and competition are untouched.
