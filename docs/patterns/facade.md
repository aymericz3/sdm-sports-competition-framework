# Facade

## Intent
Provide a single, simple entry point that hides the assembly of a complex
subsystem, so a client can obtain a fully configured object without knowing how
its parts are wired together.

## Where it lives
Building a competition means choosing and wiring six strategies, a sport profile,
an entrant list and a tiebreak chain, then handing them to the `Competition`
constructor. The discipline **modules** wrap all of that behind one call.

- [FootballModule.league(name, entrants)](../../src/com/sports/modules/football/FootballModule.java)
- [ChessModule.swiss(name, entrants)](../../src/com/sports/modules/chess/ChessModule.java)

## Participants
- **Facade:** `FootballModule`, `ChessModule`.
- **Subsystem:** `Competition` plus the six strategy implementations and the
  `Sport` profile.
- **Client:** test cases (and any library user) that just want a ready-to-run
  football league or chess Swiss.

## What the facade hides
```java
// The client writes only this:
Competition comp = FootballModule.league("Mini League", teams);

// The facade performs the whole assembly:
return new Competition(
    name, SPORT, entrants,
    new WinDrawLoss(3, 1, 0),                       // ScoringRule
    new RoundRobin(false),                          // FixtureGenerator
    new PointsTable(),                              // StandingsAggregator
    List.of(new GoalDifference(new HeadToHead(null))), // Tiebreak chain
    new TimeLimit(90),                             // TerminationRule
    new EventDerived());                           // OutcomeRule
```

The client never names the six football strategies or the order of the tiebreak
chain. Each module also exposes the discipline's `Sport` profile as a constant
(`FootballModule.SPORT`, `ChessModule.SPORT`).

## Why it matters here
The facade is the concrete embodiment of "a discipline is a bundle of strategy
objects." It keeps the [Strategy](strategy.md) wiring in exactly one place per
discipline, so the [Factory Method](factory-method.md) calls and
[Chain of Responsibility](chain-of-responsibility.md) assembly are written once
and reused by every test and caller.

## How to extend
A new discipline ships its own module class with a static factory (e.g.
`TennisModule.tournament(...)`) that assembles its strategy bundle. Callers learn
one method, not six constructors.
