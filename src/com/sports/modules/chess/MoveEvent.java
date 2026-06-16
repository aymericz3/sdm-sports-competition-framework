package com.sports.modules.chess;

import com.sports.core.contest.Participation;
import com.sports.core.contest.TerminationKind;

public class MoveEvent {
    private final Participation player;
    private final String notation;
    private final TerminationKind terminationKind; // NOT_OVER for ordinary moves

    public MoveEvent(Participation player, String notation) {
        this(player, notation, TerminationKind.NOT_OVER);
    }

    public MoveEvent(Participation player, String notation, TerminationKind terminationKind) {
        this.player = player;
        this.notation = notation;
        this.terminationKind = terminationKind;
    }

    public Participation getPlayer()           { return player; }
    public String getNotation()                { return notation; }
    public TerminationKind getTerminationKind() { return terminationKind; }
}
