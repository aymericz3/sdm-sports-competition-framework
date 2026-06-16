package com.sports.core.entity;

import java.util.UUID;

/**
 * Anything that competes: a single person or a team. The core always treats it
 * as one opaque thing; {@code isTeam} is the only distinction it carries.
 */
public class Participant {
    private final String id;
    private final String name;
    private final boolean team;

    private Participant(String id, String name, boolean team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    public static Participant individual(String name) {
        return new Participant(UUID.randomUUID().toString(), name, false);
    }

    public static Participant team(String name) {
        return new Participant(UUID.randomUUID().toString(), name, true);
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public boolean isTeam() { return team; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        return id.equals(((Participant) o).id);
    }

    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return name; }
}
