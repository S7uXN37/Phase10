package com.github.s7uxn37.phase10.constructs;

import com.github.s7uxn37.phase10.Intelligence;

public final class Target {
    public final Intelligence.TARGET_TYPE type;
    public final int cardCount;

    public Target(Intelligence.TARGET_TYPE type, int cardCount) {
        this.type = type;
        this.cardCount = cardCount;
    }

    public Target(String type, int cardCount) {
        this(Intelligence.TARGET_TYPE.valueOf(type), cardCount);
    }

    @Override
    public final String toString() {
        return type.toString() + " for " + cardCount + " cards";
    }
}
