package com.sports.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Participant {
    private final String id;
    private final String name;
    private final List<Member> roster; // null = individual; non-null = team

    private Participant(String id, String name, List<Member> roster) {
        this.id = id;
        this.name = name;
        this.roster = roster;
    }

    public static Participant individual(String name) {
        return new Participant(UUID.randomUUID().toString(), name, null);
    }

    public static Participant team(String name) {
        return new Participant(UUID.randomUUID().toString(), name, new ArrayList<>());
    }

    public static Participant withId(String id, String name, boolean isTeam) {
        return new Participant(id, name, isTeam ? new ArrayList<>() : null);
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public boolean isTeam() { return roster != null; }

    public List<Member> getRoster() {
        if (roster == null) return Collections.emptyList();
        return Collections.unmodifiableList(roster);
    }

    public void addMember(Member member) {
        if (roster == null) throw new IllegalStateException("Individual participant has no roster");
        roster.add(member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        return id.equals(((Participant) o).id);
    }

    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return name; }
}
