package com.sports.core.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Default Result implementation: an ordered list of participations plus per-participation contributions. */
public class SimpleResult implements Result {
    private final List<Participation> ranked;
    private final Map<Participation, StandingContribution> contributions;

    public SimpleResult(List<Participation> ranked,
                        Map<Participation, StandingContribution> contributions) {
        this.ranked = Collections.unmodifiableList(new ArrayList<>(ranked));
        this.contributions = Collections.unmodifiableMap(new LinkedHashMap<>(contributions));
    }

@Override
public List<Participation> getRankedParticipations() {
    return ranked;
}

@Override
public StandingContribution getContribution(Participation participation) {
    return contributions.get(participation);
}

}
  