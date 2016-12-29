package com.github.s7uxn37.phase10.constructs;

import com.github.s7uxn37.phase10.Intelligence;

public class Move {
    public Intelligence.CARD_LOCATION from;
    public Intelligence.CARD_LOCATION to;

    public Move(Intelligence.CARD_LOCATION locFrom, Intelligence.CARD_LOCATION locTo) {
        from = locFrom;
        to = locTo;
    }
}
