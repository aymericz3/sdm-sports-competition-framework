package com.sports.core.contest;

public enum TerminationKind {
    NOT_OVER,
    TIME_ELAPSED,
    CHECKMATE,
    RESIGNATION,
    DRAW_AGREED,
    THREEFOLD_REPETITION,
    STALEMATE,
    FORFEIT,
    WALKOVER,
    ABANDONED,
    CUTOFF;

    public boolean isDrawResult() {
        return this == DRAW_AGREED || this == THREEFOLD_REPETITION || this == STALEMATE;
    }
}
