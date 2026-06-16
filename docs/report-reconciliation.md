# Report-to-Implementation Reconciliation

This note records every place where the delivered code deliberately differs from
the modelling report and its class diagram, with the reason for each choice. It 
exists so the divergences are explicit decisions, not silent omissions.

The differences fall into four kinds.

## 1. Probe-only features, intentionally not implemented

The report stress-tested the model with three *probes* — a marathon (race),
tennis, and figure skating — that were explicitly **not** deliverables (report
§2, §4.3.2: "stress tests, not deliverables; only the two assigned disciplines
are realised as modules"). Features that exist in the model solely to satisfy a
probe would be dead code in a football + chess library, so they are not built:

| Report feature | Probe that motivated it | Status |
| --- | --- | --- |
| **Composite `Contest`** (child contests, sub-result roll-up; Diagram 1 `Contest o-- 0..* Contest`) | Tennis (match → set → game) | Not implemented on `Contest`. See §3 — Composite is realised at the competition tier instead. |
| `OutcomeRule` **measured / aggregated / judged** modes | Race / tennis / figure skating | Not implemented (only `EventDerived` and `Declared` ship, as §3.3.2 itself states). |
| **`Official`** capability (judged outcomes) | Figure skating | Not implemented. |
| `Participation.seed` / lane | Seeded knockout, race lanes | Not implemented (neither format is delivered). |

These are faithful to the report's own framing: the core carries the contracts
shared by *all* imaginable disciplines, and "the responsibilities a given
discipline does not use simply go unplugged" (report §5).

## 2. Optional capabilities left unplugged

The report defines some capabilities as optional, present only when a discipline
needs them. The two delivered disciplines do not, so they are left out:

- **Roster / `Member`** (report §3.1, §3.4, §4.2 "One opaque side"). This is the
  team line-up capability — relevant to **football** (a side is eleven players),
  *not* to a probe. We keep the participant fully **opaque**: a single `isTeam`
  flag carries the team-vs-individual distinction, and football scores at the
  *side* level (goals are counted for the team, not per player), so no per-player
  roster is required for the delivered scope. The capability is therefore left
  unplugged exactly as the report permits; re-introducing `Member` is additive
  and touches no core class.
- **`EventRecording` as a formal capability** (report §3.3, axes). The code uses
  a plain `List<Object> eventLog` on `Contest` plus a `Sport.hasEventLog` flag,
  rather than a separate capability type. Behaviour is identical: football's
  `EventDerived` reads the log, chess's `Declared` ignores it.

## 3. Code that goes beyond the report (driven by the implementation brief)

The implementation instructions required a **multi-level, complex competition
(the Olympics or a world championship)**. The report modelled only
`Competition ◆— 1..* Contest` and stopped there ("a league is a competition with
many contests"); it never modelled a competition *of competitions*. The code
adds that tier:

- **`Championship` / `Standings` / `OverallAggregator` / `MedalTable`**
  (`com.sports.core.competition`, `com.sports.modules.common`).

This is where the **Composite** pattern is realised — at the competition tier,
where it does real work for the brief, instead of on `Contest` for a probe
discipline that ships nothing. `Competition` is the leaf and `Championship` the
composite; both implement `Standings`, so a championship can nest other
championships (see `ChampionshipTest`). This is an intentional relocation of the
report's Composite axis, documented in
[docs/patterns/composite.md](patterns/composite.md).

## 4. Same behaviour, different representation

Minor shape differences that do not change observable behaviour:

- **Tiebreak chain.** Diagram 1 draws `Competition → 0..* Tiebreak` applied "in
  sequence." The code implements the sequence as a **Chain of Responsibility**:
  each `Tiebreak` holds a `next` link and forwards the still-tied remainder, and
  the `Competition` holds the head of the chain (`PointsTable` invokes the head).
  Ordered-sequence semantics are preserved; the assembly just lives in the chain
  rather than in the list. See
  [docs/patterns/chain-of-responsibility.md](patterns/chain-of-responsibility.md).
- **Abandoned-with-result.** The state diagram shows `ABANDONED` as a decided
  state that may carry a result. The code models "an abandoned contest that
  stands" as an explicit transition `ABANDONED → FINISHED` via
  `acceptAbandonedResult()`, rather than attaching a result to the `ABANDONED`
  state itself. The invariant "a result exists iff the contest is decided" is
  preserved.

## Summary

Nothing required by the **two assigned disciplines** is missing. The omissions
are probe-only model elements and optional capabilities the delivered
disciplines don't use; the one structural relocation (Composite from `Contest`
to `Championship`) is what lets the library answer the brief's Olympics
requirement that the report did not model.
