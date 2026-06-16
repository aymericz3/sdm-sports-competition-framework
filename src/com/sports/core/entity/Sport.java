package com.sports.core.entity;

import com.sports.core.strategy.OutcomeRule;
import com.sports.core.strategy.TerminationRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * The discipline itself, acting as a profile: it names the roles a contest may
 * use, whether an event log is recorded, and the default rules a contest in this
 * discipline runs under. It supplies the default {@link TerminationRule} and
 * {@link OutcomeRule}; it deliberately does NOT own the points system — that
 * belongs to the {@code Competition}, so the same sport can be scored differently
 * in different tournaments.
 *
 * <p>The rules are injected as suppliers by the discipline module, which keeps
 * the core free of any dependency on concrete module classes.
 */
public class Sport {
    private final String name;
    private final List<String> allowedRoles; // e.g. ["HOME","AWAY"] or ["WHITE","BLACK"]
    private final boolean hasEventLog;        // whether events are recorded
    private final Supplier<TerminationRule> defaultTermination;
    private final Supplier<OutcomeRule> defaultOutcome;

    public Sport(String name,
                 List<String> allowedRoles,
                 boolean hasEventLog,
                 Supplier<TerminationRule> defaultTermination,
                 Supplier<OutcomeRule> defaultOutcome) {
        this.name = name;
        this.allowedRoles = new ArrayList<>(allowedRoles);
        this.hasEventLog = hasEventLog;
        this.defaultTermination = defaultTermination;
        this.defaultOutcome = defaultOutcome;
    }

    public String getName()               { return name; }
    public List<String> getAllowedRoles() { return Collections.unmodifiableList(allowedRoles); }
    public boolean hasEventLog()          { return hasEventLog; }

    /** True if {@code role} is part of this discipline's allowed set (null = no role used). */
    public boolean isAllowedRole(String role) {
        return role == null || allowedRoles.contains(role);
    }

    /** A fresh instance of this discipline's default termination rule. */
    public TerminationRule newTermination() { return defaultTermination.get(); }

    /** A fresh instance of this discipline's default outcome rule. */
    public OutcomeRule newOutcome() { return defaultOutcome.get(); }

    @Override public String toString() { return name; }
}
