package com.github.s7uxn37.phase10.constructs;

import com.github.s7uxn37.phase10.Intelligence;

public class Target {
    Intelligence.TARGET_TYPE type;
    int arg;

    public Target(Intelligence.TARGET_TYPE type, int arg) {
        this.type = type;
        this.arg = arg;
    }

    public Target(String type, int arg) {
        this(Intelligence.TARGET_TYPE.valueOf(type), arg);
    }
}
