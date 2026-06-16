# Sports Competition Framework

A Java library for planning, scheduling and tracking sports events — from a
single contest, through a multi-contest competition (league, Swiss), up to a
multi-level championship (an Olympics / world championship aggregating many
events). It has **no persistence layer and no UI**: it provides the objects and
concepts needed to run a competition, and is exercised through its test suite.

The design follows the modelling report (*Software Design and Modeling*, B.
Walter): a closed **core** of universal concepts plus additive **discipline
modules** (football, chess) that contribute only strategy implementations. Adding
a discipline means writing strategies, not editing the core.

## Requirements
- JDK 11+
- No build tool required; JUnit 5 is vendored in [`lib/`](lib/).

## Build & test
```bash
./build.sh   # compile the library into out/
./test.sh    # compile everything and run the JUnit 5 suite
```
This is a library, so "running the system" means running the tests — they create,
configure and drive real competitions (`./test.sh` builds a football league and a
chess Swiss and asserts the resulting standings).

## Architecture

### Core (`src/com/sports/core`) — closed, discipline-agnostic
| Concept | Role |
| --- | --- |
| `entity/Participant`, `entity/Sport` | The two entities that survive every discipline. A participant is opaque (individual or team); `Sport` is the profile that supplies the discipline's default rules and allowed roles. |
| `contest/Contest` | A self-contained encounter; tracks its state machine and produces one `Result`. |
| `contest/Participation` | **Association class** linking a participant to a contest; carries role + outcome, and points back to both. |
| `contest/Result` (+ `SimpleResult`) | The only many-shapes interface: an ordering plus a `StandingContribution` per participation. |
| `contest/StandingContribution` | Sport-agnostic token (`won`, `drew`, `3rd`, …) read by scoring. |
| `competition/Competition`, `competition/Leaderboard` | A leaf `Standings`: groups entrants under a format; turns results into points; computes standings on demand. |
| `competition/Championship` (+ `Standings`) | The **Composite** node: a multi-level competition aggregating child `Standings` into one overall table. |
| `strategy/*` | The pluggable rule interfaces (see below), including `OverallAggregator` for the championship tier. |

### Modules (`src/com/sports/modules`) — additive
- `football/` — `TimeLimit`, `EventDerived`, `RoundRobin`, `GoalDifference`,
  `HeadToHead`, `FootballModule`.
- `chess/` — `TerminalCondition`, `Declared`, `SwissPairing`, `Buchholz`,
  `SonnebornBerger`, `ChessModule`.
- `common/` — `WinDrawLoss`, `PointsTable`, `MedalTable` (shared).

No module subclasses `Contest`, `Result` or introduces a `Stats` class — that
absence is the design point. Standings are always recomputed from decided
results, the scoring rule and the tiebreak chain; nothing is cached.

### A discipline is a bundle of strategies
| | Football | Chess |
| --- | --- | --- |
| Termination | `TimeLimit(90)` | `TerminalCondition` |
| Outcome | `EventDerived` | `Declared` |
| Scoring | `WinDrawLoss(3,1,0)` | `WinDrawLoss(1,0.5,0)` |
| Fixtures | `RoundRobin` | `SwissPairing` |
| Standings | `PointsTable` | `PointsTable` |
| Tiebreak | `GoalDifference → HeadToHead` | `Buchholz → SonnebornBerger` |

The framework code is identical across both; only the injected objects change.

## Design patterns
Five patterns carry the design. Each has its own write-up in
[`docs/patterns/`](docs/patterns/):

| Pattern | Role in the framework |
| --- | --- |
| [Strategy](docs/patterns/strategy.md) | The six rule interfaces — the mechanism that replaces football/chess subclasses. |
| [Chain of Responsibility](docs/patterns/chain-of-responsibility.md) | The tiebreak chain, each criterion forwarding the still-tied remainder. |
| [Composite](docs/patterns/composite.md) | `Championship` aggregates child `Standings` (competitions or sub-championships) into one medal table — the multi-level competition. |
| [Facade](docs/patterns/facade.md) | `FootballModule` / `ChessModule` assemble a whole competition behind one call. |
| [Factory Method](docs/patterns/factory-method.md) | Named static factories on `Participant` and `StandingContribution`. |

## Multi-level competitions
A single `Competition` (league, Swiss) is a leaf; a `Championship` composes
several of them — and may nest other championships — into a Games. The overall
ranking is a pluggable `OverallAggregator` (`MedalTable` counts golds across
events). `ChampionshipTest` builds a mini-Olympics of one football league and one
chess event over the same nations and asserts the resulting medal table.

## Project layout
```
src/com/sports/
  core/      entities, contest, competition, strategy interfaces
  modules/   football, chess, common
test/com/sports/   JUnit 5 suite (core + modules)
docs/patterns/     one file per design pattern
lib/               vendored JUnit 5 console launcher
build.sh test.sh   compile / test
```
