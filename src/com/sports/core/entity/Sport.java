package com.sports.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sport {
    private final String name;
    private final List<String> allowedRoles; // e.g. ["HOME","AWAY"] or ["WHITE","BLACK"]
    private final boolean hasEventLog;       // whether events are recorded
    private final boolean hasRoster;         // whether participants expose member rosters

    public Sport(String name, List<String> allowedRoles, boolean hasEventLog, boolean hasRoster) {
        this.name = name;
        this.allowedRoles = new ArrayList<>(allowedRoles);
        this.hasEventLog = hasEventLog;
        this.hasRoster = hasRoster;
    }

    public String getName()                  { return name; }
    public List<String> getAllowedRoles()    { return Collections.unmodifiableList(allowedRoles); }
    public boolean hasEventLog()             { return hasEventLog; }
    public boolean hasRoster()               { return hasRoster; }

    @Override public String toString() { return name; }
}
