# Factory Method

## Intent
Expose named, intention-revealing methods that build and return fully formed
objects, instead of leaking construction details through public constructors.
Each method names the *kind* of object it produces and guarantees the produced
object's invariants.

## Where it lives
Two entities use static factory methods to make construction self-explanatory and
to keep their internal representation private.

### Participant — individual vs team
[Participant.java](../../src/com/sports/core/entity/Participant.java) has a
`private` constructor; callers pick a named factory. The "is it a team?" decision
is encoded once, by the factory, never re-derived by callers.

```java
Participant.individual("Magnus");   // isTeam == false
Participant.team("Brazil");         // isTeam == true
```

This is what keeps a participant **opaque**: the core never branches on
team-versus-individual because the factory already settled it, and the
distinction is a single flag rather than a leaked structure.

### StandingContribution — the sport-agnostic tokens
[StandingContribution.java](../../src/com/sports/core/contest/StandingContribution.java)
offers one factory per meaningful outcome token, so `OutcomeRule` implementations
read like the domain instead of juggling raw `(position, token, completed)`
triples.

```java
StandingContribution.won();        // (1, "won",  true)
StandingContribution.drew();       // (0, "drew", true)
StandingContribution.lost();       // (2, "lost", true)
StandingContribution.position(3);  // (3, "3rd",  true)
StandingContribution.dnf(pos);     // (pos, "DNF", false)
StandingContribution.dsq(pos);     // (pos, "DSQ", false)
```

## Participants
- **Creator:** `Participant`, `StandingContribution` (private constructors,
  static factories).
- **Products:** a configured `Participant`; a valid `StandingContribution`.
- **Clients:** `OutcomeRule` implementations (`EventDerived`, `Declared`) and
  test setup, which name the outcome rather than constructing it field by field.

## Why it matters here
The factories concentrate each type's invariants in one place: a participant is
always consistently "individual or team," and a contribution always carries a
coherent `(position, token, completed)`. `OutcomeRule` code stays readable and
the value types stay immutable.

## How to extend
A new outcome shape (e.g. a measured time placing) adds a new factory such as
`StandingContribution.position(pos)` rather than a new public constructor, so
existing call sites and invariants are unaffected.
