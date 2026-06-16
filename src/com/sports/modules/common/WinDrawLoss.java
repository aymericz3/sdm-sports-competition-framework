package com.sports.modules.common;

import com.sports.core.contest.StandingContribution;
import com.sports.core.strategy.ScoringRule;

public class WinDrawLoss implements ScoringRule {
    private final double winPoints;
    private final double drawPoints;
    private final double lossPoints;

    public WinDrawLoss(double win, double draw, double loss) {
        this.winPoints = win;
        this.drawPoints = draw;
        this.lossPoints = loss;
    }

    @Override
    public double points(StandingContribution contribution) {
        if (contribution == null) {
            return 0;
        }

        String token = contribution.getToken();

        if ("won".equals(token)) {
            return winPoints;
        }

        if ("drew".equals(token)) {
            return drawPoints;
        }

        if ("lost".equals(token)) {
            return lossPoints;
        }

        return 0;
    }
}