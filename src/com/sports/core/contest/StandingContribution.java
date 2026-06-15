package com.sports.core.contest;

public final class StandingContribution {
    private final int position;      // 1-based ranking; 0 = not ranked
    private final String token;      // "won", "drew", "lost", "1st", "DNF", etc.
    private final boolean completed; // false for DNF / DSQ

    public StandingContribution(int position, String token, boolean completed) {
        this.position = position;
        this.token = token;
        this.completed = completed;
    }

    public static StandingContribution won()  { return new StandingContribution(1, "won",  true); }
    public static StandingContribution drew() { return new StandingContribution(0, "drew", true); }
    public static StandingContribution lost() { return new StandingContribution(2, "lost", true); }

    public static StandingContribution position(int pos) {
        return new StandingContribution(pos, ordinal(pos), true);
    }

    public static StandingContribution dnf(int pos) {
        return new StandingContribution(pos, "DNF", false);
    }

    public static StandingContribution dsq(int pos) {
        return new StandingContribution(pos, "DSQ", false);
    }

    private static String ordinal(int n) {
        if (n == 1) return "1st";
        if (n == 2) return "2nd";
        if (n == 3) return "3rd";
        return n + "th";
    }

    public int getPosition()    { return position; }
    public String getToken()    { return token; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        return token + (position > 0 ? "(" + position + ")" : "");
    }
}
